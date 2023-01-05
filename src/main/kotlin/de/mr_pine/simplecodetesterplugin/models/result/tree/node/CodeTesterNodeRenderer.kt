package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.ide.ui.UISettings
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.ui.RelativeFont
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Graphics
import java.awt.Shape
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

class CodeTesterNodeRenderer: NodeRenderer() {
    private var durationText: String? = null
    private var durationColor: Color? = null
    private var durationWidth = 0
    private var durationOffset = 0

    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus)
        durationText = null
        durationColor = null
        durationWidth = 0
        durationOffset = 0
        val node = value as DefaultMutableTreeNode
        val userObj = node.userObject
        if (userObj is ResultTreeNode) {
            durationText = userObj.duration?.toString()
            if (durationText != null) {
                val metrics = getFontMetrics(RelativeFont.SMALL.derive(font))
                durationWidth = metrics.stringWidth(durationText!!)
                durationOffset = metrics.height / 2 // an empty area before and after the text
                durationColor =
                    if (selected) UIUtil.getTreeSelectionForeground(hasFocus) else SimpleTextAttributes.GRAYED_ATTRIBUTES.fgColor
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        UISettings.setupAntialiasing(g)
        var clip: Shape? = null
        var width = width
        val height = height
        if (isOpaque) {
            // paint background for expanded row
            g.color = background
            g.fillRect(0, 0, width, height)
        }
        if (durationWidth > 0) {
            width -= durationWidth + durationOffset
            if (width > 0 && height > 0) {
                g.color = durationColor
                g.font = RelativeFont.SMALL.derive(font)
                g.drawString(durationText!!, width + durationOffset / 2, getTextBaseLine(g.fontMetrics, height))
                clip = g.clip
                g.clipRect(0, 0, width, height)
            }
        }
        super.paintComponent(g)
        // restore clip area if needed
        if (clip != null) g.clip = clip
    }
}