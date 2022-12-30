package de.mr_pine.simplecodetesterplugin

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterGetCategoriesAction
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object CodeTester {
    private val logger = Logger.getInstance(CodeTester.javaClass).apply { setLevel(LogLevel.DEBUG) }

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
                        val tokenInfo: TokenInfo = client.submitForm(
                            url = "$url/login/get-access-token",
                            formParameters = Parameters.build {
                                append("refreshToken", refreshToken)
                            }
                        ) { markAsRefreshTokenRequest() }.body()

                        CodeTesterCredentials[CodeTesterCredentials.CredentialType.ACCESS_TOKEN] = tokenInfo.token

                        BearerTokens(refreshToken = refreshToken, accessToken = tokenInfo.token)
                    }
                }
            }
        }
    }

    @Serializable
    private data class TokenInfo(
        val token: String,
        val displayName: String,
        val userName: String,
        val roles: List<String>
    )

    suspend fun login(username: String, password: String) {
        val loginInfo: LoginInfo = client.submitForm(
            url = "$url/login",
            formParameters = Parameters.build {
                append("username", username)
                append("password", password)
            }
        ).body()

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

    private val loginListeners: MutableList<() -> Unit> = mutableListOf(
        {
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
        }
    )

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
        categories = client.get(
            "$url/check-category/get-all"
        ).body<List<TestCategory>>().reversed()

        logger.warn(categories.toString())
    }

    var categories: List<TestCategory> = listOf()
    var currentCategory: TestCategory? = null

    suspend fun submitFiles(
        category: TestCategory,
        files: List<VirtualFile>
    ): CodeTesterResult {
        val response = client.submitFormWithBinaryData(
            url = "$url/test/multiple/${category.id}",
            formData {
                files.forEach { file ->
                    append(file.name, file.contentsToByteArray(), Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                }
            }
        )

        return response.body()
    }

    init {
        logger.info("refreshToken: ${CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN].toString()}")

        if (CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] != null) loginListeners.forEach {
            ApplicationManager.getApplication().invokeLater(it)
        }
    }
}