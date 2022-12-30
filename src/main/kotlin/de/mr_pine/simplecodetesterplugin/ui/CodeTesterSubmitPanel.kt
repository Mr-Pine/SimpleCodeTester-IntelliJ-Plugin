package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import javax.swing.JPanel

class CodeTesterSubmitPanel(val project: Project) : JPanel(BorderLayout()) {
    private var toolWindowPanel: CodeTesterToolWindowPanel? = null

    init {
        createToolPanel()
        add(toolWindowPanel!!.getPanel())
        isVisible = true
    }

    private fun createToolPanel() {
        toolWindowPanel = CodeTesterToolWindowPanel(
            this::class.java,
            topComponent = CodeTesterActionToolBar(ToolBarOrientation.HORIZONTAL).component,
            mainComponent = JBLabel("Nothing to see here (yet)")
        )
    }
}