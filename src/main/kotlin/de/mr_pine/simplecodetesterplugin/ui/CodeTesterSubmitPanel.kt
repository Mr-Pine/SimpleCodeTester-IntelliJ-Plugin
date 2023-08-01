package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import javax.swing.JPanel

class CodeTesterSubmitPanel : JPanel(BorderLayout()) {
    init {
        val label = JBLabel("Nothing to see here (yet)")
        val toolWindowPanel = CodeTesterToolWindowPanel(
            topComponent = CodeTesterActionToolBar(ToolBarOrientation.HORIZONTAL).apply { setTargetComponent(label) }.component,
            mainComponent = label
        )
        add(toolWindowPanel.getPanel())
        isVisible = true
    }
}