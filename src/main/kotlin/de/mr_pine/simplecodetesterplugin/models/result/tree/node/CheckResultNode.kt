package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project
import de.mr_pine.simplecodetesterplugin.models.result.OutputLine
import kotlin.time.Duration

class CheckResultNode(
    project: Project,
    parentNode: FileResultNode,
    checkName: String,
    override var duration: Duration?,
    override val success: Boolean,
    val content: List<OutputLine>
) : ResultTreeNode(project, parentNode) {
    override val title = checkName
}