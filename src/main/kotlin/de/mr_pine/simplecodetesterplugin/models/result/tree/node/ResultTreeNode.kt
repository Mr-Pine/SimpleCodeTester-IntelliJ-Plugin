package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.SimpleTextAttributes
import de.mr_pine.simplecodetesterplugin.models.result.OutputLine
import javax.swing.Icon
import kotlin.time.Duration

open class ResultTreeNode(project: Project, val parentNode: ResultTreeNode?, open var duration: Duration? = null) :
    PresentableNodeDescriptor<ResultTreeNode>(project, parentNode) {
    val children = mutableListOf<ResultTreeNode>()

    open val hint: String? = null
    open val nodeName: String? = null
    open val success = false
    open val title: String? = null

    override fun update(presentation: PresentationData) {
        presentation.apply {
            icon = getCurrentIcon()
            presentableText = nodeName
            this.setIcon(icon)

            nodeName?.takeIf { it.isNotBlank() }?.let {
                addText(
                    (if (hint?.isBlank() == true) "" else ": ") + it,
                    SimpleTextAttributes.REGULAR_ATTRIBUTES
                )
            }
            title?.takeIf { it.isNotBlank() }?.let { addText(it, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES) }
            hint?.takeIf { it.isNotBlank() }?.let {
                addText(
                    (if (nodeName?.isBlank() == true) "" else " ") + hint,
                    SimpleTextAttributes.GRAYED_ATTRIBUTES
                )
            }
        }
    }

    override fun getElement() = this


    open val output: List<OutputLine> = listOf()

    fun add(child: ResultTreeNode) {
        children.add(child)
    }

    open fun getCurrentIcon() =
        if (success) NODE_ICON_OK else NODE_ICON_ERROR

    companion object {
        private val NODE_ICON_OK: Icon = AllIcons.RunConfigurations.TestPassed
        private val NODE_ICON_ERROR: Icon = AllIcons.RunConfigurations.TestError

        @JvmStatic
        protected val NODE_ICON_TIMEOUT: Icon = AllIcons.RunConfigurations.TestSkipped

        @JvmStatic
        protected val NODE_ICON_RUNNING: Icon = AnimatedIcon.Default()
    }
}