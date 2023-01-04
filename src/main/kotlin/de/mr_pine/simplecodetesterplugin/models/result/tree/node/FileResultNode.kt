package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project
import kotlin.time.Duration

class FileResultNode(project: Project, parentNode: ResultTreeNode, fileName: String) :
    ResultTreeNode(project, parentNode) {
    override val hint = fileName
    override val nodeName = fileName
    override var duration: Duration
        get() = children.fold(Duration.ZERO) { total, child -> total + child.duration }
        set(_) {}
    override val success: Boolean
        get() = children.all { it.success }
}