package de.mr_pine.simplecodetesterplugin

import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

object CodeTester {
    private val logger = Logger.getInstance(CodeTester.javaClass).apply { setLevel(LogLevel.DEBUG) }

    private const val url = "https://codetester.ialistannen.de"
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
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

    init {
        logger.info("refreshToken: ${CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN].toString()}")
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
        logger.debug("Login: ${loginInfo.token}")
    }

    @Serializable
    private data class LoginInfo(
        val token: String
    )

    val loggedIn: Boolean
        get() = CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] != null

    fun logOut() {
        CodeTesterCredentials[CodeTesterCredentials.CredentialType.REFRESH_TOKEN] = null
        CodeTesterCredentials[CodeTesterCredentials.CredentialType.ACCESS_TOKEN] = null
    }

    suspend fun getCategories() {
        val categories: List<CategoryData> = client.get(
            "$url/check-category/get-all"
        ).body()

        logger.warn(categories.toString())
    }

    @Serializable
    private data class CategoryData(
        val id: Int,
        val name: String
    )
}