package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class CodeTesterToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(createDialogPanel(), "Test 123", false)
        contentManager.addContent(content)
    }
}