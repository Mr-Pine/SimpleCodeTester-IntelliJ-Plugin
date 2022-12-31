package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project

class RootResultNode(project: Project, parentNode: ResultTreeNode?): ResultTreeNode(project, parentNode) {
    var finished = false
    override val success: Boolean
        get() = children.all { it.success }

    fun finish() {
        finished = true
    }

    override fun getCurrentIcon() = if(finished) super.getCurrentIcon() else NODE_ICON_RUNNING
}