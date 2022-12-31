package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import de.mr_pine.simplecodetesterplugin.CodeTester
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterResultPanel
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterSubmitPanel

class CodeTesterToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        val contentManager = toolWindow.contentManager
        val loggedOutContent = contentManager.factory.createContent(loggedOutDialogPanel(), null, false)
        val submitContent = contentManager.factory.createContent(CodeTesterSubmitPanel(project), "Submit", false)

        fun showSubmitContent() {
            contentManager.removeContent(loggedOutContent, true)
            contentManager.addContent(submitContent)
        }
        fun showLoggedOutContent() {
            contentManager.removeContent(submitContent, true)
            contentManager.addContent(loggedOutContent)
        }
        fun showResultContent(result: CodeTesterResult) {
            val resultContent = contentManager.factory.createContent(CodeTesterResultPanel(project, result).component, "Result", false)
            contentManager.addContent(resultContent)
        }

        CodeTester.registerLoginListener(::showSubmitContent)
        CodeTester.registerLogoutListener(::showLoggedOutContent)
        CodeTester.registerResultListener(::showResultContent)

        if (CodeTester.loggedIn) {
            showSubmitContent()
        } else {
            contentManager.addContent(loggedOutContent)
        }

    }
}