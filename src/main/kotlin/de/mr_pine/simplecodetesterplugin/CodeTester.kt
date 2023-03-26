package de.mr_pine.simplecodetesterplugin

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterGetCategoriesAction
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.models.result.TestCategory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

object CodeTester {
    private const val testIdProperty = "de.mr_pine.simplecodetesterplugin.testId"

    private val logger = Logger.getInstance(CodeTester.javaClass).apply { setLevel(LogLevel.DEBUG) }
    private var propertiesComponent: PropertiesComponent? = null

    private const val url = "https://codetester.ialistannen.de"
    private val client = HttpClient(Java) {
        engine {
            protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = CodeTesterCredentials[CodeTesterCredentials.CredentialType.ACCESS_TOKEN]
                    val refreshToken = CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN]

                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken = accessToken, refreshToken = refreshToken)
                    } else {
                        null
                    }
                }
                refreshTokens {
                    CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN]?.let { refreshToken ->
                        val refreshResult =
                            client.submitForm(url = "$url/login/get-access-token", formParameters = Parameters.build {
                                append("refreshToken", refreshToken)
                            }) { markAsRefreshTokenRequest() }

                        if (refreshResult.status == HttpStatusCode.Unauthorized) {
                            logOut()
                            null
                        } else {
                            val tokenInfo: TokenInfo = refreshResult.body()
                            CodeTesterCredentials[CodeTesterCredentials.CredentialType.ACCESS_TOKEN] = tokenInfo.token

                            BearerTokens(refreshToken = refreshToken, accessToken = tokenInfo.token)
                        }
                    }
                }
            }
        }
    }

    @Serializable
    private data class TokenInfo(
        val token: String, val displayName: String, val userName: String, val roles: List<String>
    )

    suspend fun login(username: String, password: String) {
        val loginInfo: LoginInfo = client.submitForm(url = "$url/login", formParameters = Parameters.build {
            append("username", username)
            append("password", password)
        }).body()

        CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] = loginInfo.token
        loginListeners.forEach { ApplicationManager.getApplication().invokeLater(it) }
        logger.debug("Login: ${loginInfo.token}")
    }

    @Serializable
    private data class LoginInfo(
        val token: String
    )

    val loggedIn: Boolean
        get() = CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] != null

    private val loginListeners: MutableList<() -> Unit> = mutableListOf({
        CodeTesterGetCategoriesAction().actionPerformed(
            AnActionEvent(
                null,
                DataContext.EMPTY_CONTEXT,
                ActionPlaces.UNKNOWN,
                Presentation(),
                ActionManager.getInstance(),
                0
            )
        )
    })

    val registerLoginListener: (() -> Unit) -> Boolean = loginListeners::add

    fun logOut() {
        CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] = null
        CodeTesterCredentials[CodeTesterCredentials.CredentialType.ACCESS_TOKEN] = null
        categories = listOf()

        logoutListeners.forEach { ApplicationManager.getApplication().invokeLater(it) }
    }

    private val logoutListeners: MutableList<() -> Unit> = mutableListOf()
    val registerLogoutListener: (() -> Unit) -> Boolean = logoutListeners::add

    suspend fun getCategories() {
        val result = client.get(
            "$url/check-category/get-all"
        )
        if (result.status != HttpStatusCode.Unauthorized) {
            categories = result.body<List<TestCategory>>().reversed()

            logger.warn(categories.toString())
        }
    }

    var categories: List<TestCategory> = listOf()
    var currentCategory: TestCategory? = null
        set(value) {
            field = value
            if (value != null) propertiesComponent?.setValue(testIdProperty, value.id, -1)
        }

    suspend fun submitFiles(
        category: TestCategory, files: List<VirtualFile>
    ): SharedFlow<Result<CodeTesterResult>> = coroutineScope {
        val resultFlow: MutableSharedFlow<Result<CodeTesterResult>> = MutableSharedFlow()
        launch {

            try {
                val response = client.submitFormWithBinaryData(url = "$url/test/multiple/${category.id}", formData {
                    files.forEach { file ->
                        append(file.name, file.contentsToByteArray(), Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                        })
                    }
                })

                println(response.body<String>())

                try {
                    val result = try {
                        response.body<CodeTesterResult>().apply {
                                duration =
                                    (response.responseTime.timestamp - response.requestTime.timestamp).milliseconds
                            }
                    } catch (e: JsonConvertException) { // Please forward any complaints to I-Al-Istannen
                        CodeTesterResult(compilationOutput = response.body())
                    }

                    result.let { resultFlow.emit(Result.success(it)) }
                } catch (e: Exception) {
                    val exception = SubmitException(e, response.body())
                    resultFlow.emit(Result.failure(exception))
                }


            } catch (e: Exception) {
                resultFlow.emit(Result.failure(SubmitException(e)))
            }

        }
        resultFlowListeners.forEach { ApplicationManager.getApplication().invokeLater { it(resultFlow, category) } }
        return@coroutineScope resultFlow
    }

    class SubmitException(msg: String) : Exception(msg) {
        constructor(
            e: Exception, responseString: String
        ) : this("Exception ${e::class.qualifiedName} occurred during response parsing of \n${responseString}\n Caused by: ${e.cause} \n\n\nStacktrace: \n${e.stackTraceToString()}")

        constructor(e: Exception) : this("Error ${e::class.qualifiedName} occured: ${e.message}\nat: ${e.stackTraceToString()}")
    }

    private val resultFlowListeners: MutableList<(SharedFlow<Result<CodeTesterResult>>, TestCategory) -> Unit> =
        mutableListOf()
    val registerResultFlowListener: ((SharedFlow<Result<CodeTesterResult>>, TestCategory) -> Unit) -> Boolean =
        resultFlowListeners::add

    fun loadProperties(project: Project?) {
        propertiesComponent = project?.let { PropertiesComponent.getInstance(it) } ?: PropertiesComponent.getInstance()
        val testId = propertiesComponent?.getInt(testIdProperty, -1)?.takeIf { it >= 0 }
        val category = categories.find { it.id == testId }
        if (testId != null && category != null) {
            currentCategory = category
        }
    }

    init {
        logger.info("refreshToken: ${CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN].toString()}")

        if (CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] != null) loginListeners.forEach {
            ApplicationManager.getApplication().invokeLater(it)
        }
    }
}