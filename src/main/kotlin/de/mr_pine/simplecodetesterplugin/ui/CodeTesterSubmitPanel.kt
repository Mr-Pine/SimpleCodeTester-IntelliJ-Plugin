package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import de.mr_pine.simplecodetesterplugin.actions.CategorySelectionComboBoxAction
import java.awt.BorderLayout
import javax.swing.Box
import javax.swing.JPanel

class CodeTesterSubmitPanel(val project: Project) : JPanel(BorderLayout()) {
    private var toolWindowPanel: CodeTesterToolWindowPanel? = null

    init {
        createToolPanel()
        add(toolWindowPanel!!.getPanel())
        isVisible = true
    }

    companion object {
        const val ID_MAIN_TOOL_WINDOW = "CodeTester"
        private const val MAIN_ACTION_GROUP = "CodeTesterPluginActions"
        private val LOG: Logger = Logger.getInstance(this::class.java)
    }

    private fun createToolPanel() {
        toolWindowPanel = CodeTesterToolWindowPanel(
            this::class.java,
            topComponent = CodeTesterActionToolBar(ToolBarOrientation.HORIZONTAL).component,
            mainComponent = JBLabel("Nothing to see here (yet)")
        )
    }
}