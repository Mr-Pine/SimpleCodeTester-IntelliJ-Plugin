package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import de.mr_pine.simplecodetesterplugin.CodeTester
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.models.result.TestCategory
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterResultPanel
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterSubmitPanel
import kotlinx.coroutines.flow.Flow

class CodeTesterToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        val contentManager = toolWindow.contentManager
        val loggedOutContent = contentManager.factory.createContent(loggedOutDialogPanel(), null, false).apply {
            isCloseable = false
        }
        val submitContent =
            contentManager.factory.createContent(CodeTesterSubmitPanel(project), "Submit", false).apply {
                isCloseable = false
            }

        fun showSubmitContent() {
            contentManager.addContent(submitContent)
            contentManager.removeContent(loggedOutContent, true)
        }

        fun showLoggedOutContent() {
            contentManager.removeContent(submitContent, true)
            contentManager.addContent(loggedOutContent)
        }

        fun showResultContent(resultFlow: Flow<Result<CodeTesterResult>>, testCategory: TestCategory) {
            val resultPanel = CodeTesterResultPanel(project, resultFlow, testCategory)
            val resultContent = contentManager.factory.createContent(resultPanel.component, "Result", false).apply {
                preferredFocusableComponent = resultPanel.preferredFocusableComponent
            }
            contentManager.addContent(resultContent)
            contentManager.setSelectedContent(resultContent)
        }

        CodeTester.registerLoginListener(::showSubmitContent)
        CodeTester.registerLogoutListener(::showLoggedOutContent)
        CodeTester.registerResultFlowListener(::showResultContent)

        if (CodeTester.loggedIn) {
            showSubmitContent()
        } else {
            contentManager.addContent(loggedOutContent)
        }

    }
}