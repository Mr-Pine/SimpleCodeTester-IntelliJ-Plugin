package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import de.mr_pine.simplecodetesterplugin.CodeTester

class CodeTesterLogoutAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) = CodeTester.logOut()

    override fun update(event: AnActionEvent) {
        event.presentation.isVisible = CodeTester.loggedIn
    }
}