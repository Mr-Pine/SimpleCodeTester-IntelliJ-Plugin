package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.BorderLayout
import javax.swing.JComponent

class CodeTesterToolWindowPanel(
    private val topComponent: JComponent? = null,
    private val leftComponent: JComponent? = null,
    private val mainComponent: JComponent? = null,
    private val rightComponent: JComponent? = null
) {

    private val basePanel = BorderLayoutPanel(1, 1).apply {
        mainComponent?.let { add(it, BorderLayout.CENTER) }
        topComponent?.let { add(it, BorderLayout.NORTH) }
        leftComponent?.let { add(it, BorderLayout.WEST) }
        rightComponent?.let { add(it, BorderLayout.EAST) }
    }

    fun getPanel() = basePanel

}