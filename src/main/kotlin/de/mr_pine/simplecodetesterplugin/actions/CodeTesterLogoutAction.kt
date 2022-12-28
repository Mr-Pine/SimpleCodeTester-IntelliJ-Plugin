package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import de.mr_pine.simplecodetesterplugin.CodeTester

class CodeTesterLogoutAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) = CodeTester.logOut()

    override fun update(event: AnActionEvent) {
        event.presentation.isVisible = CodeTester.loggedIn
    }
}