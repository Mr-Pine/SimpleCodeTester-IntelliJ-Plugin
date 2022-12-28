package de.mr_pine.simplecodetesterplugin

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe


object CodeTesterCredentials {
    private fun createCredentialAttributes(credentialType: CredentialType): CredentialAttributes {
        return CredentialAttributes(
            generateServiceName("CodeTester", credentialType.toString())
        )
    }

    private fun setCredentials(credentialType: CredentialType, value: String?) {
        val credentialAttributes = createCredentialAttributes(credentialType)
        val credentials = Credentials(value)
        PasswordSafe.instance[credentialAttributes] = credentials
    }

    private fun getCredentials(credentialType: CredentialType): String? {
        val credentialAttributes = createCredentialAttributes(credentialType)
        val credentials = PasswordSafe.instance[credentialAttributes]
        return credentials?.userName
    }

    operator fun get(credentialType: CredentialType) = getCredentials(credentialType)
    operator fun set(credentialType: CredentialType, value: String?) = setCredentials(credentialType, value)

    enum class CredentialType {
        REFRESH_TOKEN, ACCESS_TOKEN
    }
}