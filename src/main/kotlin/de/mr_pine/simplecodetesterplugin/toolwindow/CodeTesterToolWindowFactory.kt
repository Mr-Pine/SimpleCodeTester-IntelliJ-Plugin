package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import de.mr_pine.simplecodetesterplugin.CodeTester

class CodeTesterToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val loggedOutContent = contentManager.factory.createContent(loggedOutDialogPanel(), null, false)
        val submitContent = contentManager.factory.createContent(loggedInDialogPanel(), null, false)

        fun showSubmitContent() {
            contentManager.removeContent(loggedOutContent, true)
            contentManager.addContent(submitContent)
        }
        fun showLoggedOutContent() {
            contentManager.removeContent(submitContent, true)
            contentManager.addContent(loggedOutContent)
        }

        CodeTester.registerLoginListener(::showSubmitContent)
        CodeTester.registerLogoutListener(::showLoggedOutContent)

        if (CodeTester.loggedIn) {
            showSubmitContent()
        } else {
            contentManager.addContent(loggedOutContent)
        }

    }
}