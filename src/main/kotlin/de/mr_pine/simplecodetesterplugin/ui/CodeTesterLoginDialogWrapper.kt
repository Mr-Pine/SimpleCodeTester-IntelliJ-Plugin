package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.credentialStore.askPassword
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent
import javax.swing.JPasswordField

class CodeTesterLoginDialogWrapper: DialogWrapper(true) {

    init {
        title = "Log in to CodeTester"
        init()
    }
    override fun createCenterPanel(): JComponent = panel {
        val passwordField = JPasswordField()
        row {
            label("Username")
            textField().align(AlignX.FILL)
        }
        row {
            label("Password")
            cell(passwordField).align(AlignX.FILL)
        }
    }
}