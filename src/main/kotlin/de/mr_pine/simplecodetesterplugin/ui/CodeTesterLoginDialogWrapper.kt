package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JPasswordField

class CodeTesterLoginDialogWrapper: DialogWrapper(true) {

    var username = ""
    var password = ""

    init {
        title = "Log in to CodeTester"
        init()
    }

    override fun createCenterPanel(): JComponent = panel {
        val passwordField = JPasswordField()
        row {
            label("Username")
            textField().align(AlignX.FILL).bindText(this@CodeTesterLoginDialogWrapper::username)
        }
        row {
            label("Password")
            cell(passwordField).align(AlignX.FILL).bindText(this@CodeTesterLoginDialogWrapper::password)
        }
    }
}