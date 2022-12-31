package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project
import kotlin.time.Duration

class CheckResultNode(
    project: Project,
    parentNode: FileResultNode,
    checkName: String,
    override val duration: Duration,
    override val success: Boolean
) : ResultTreeNode(project, parentNode) {
    override val hint = checkName
    override val nodeName = checkName
}