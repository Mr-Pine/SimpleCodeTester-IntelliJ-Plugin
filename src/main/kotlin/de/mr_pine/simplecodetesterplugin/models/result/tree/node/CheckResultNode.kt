package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project
import de.mr_pine.simplecodetesterplugin.models.result.OutputLine
import kotlin.time.Duration

class CheckResultNode(
    project: Project,
    parentNode: FileResultNode,
    override val title: String,
    override var duration: Duration?,
    override val success: Boolean,
    override val output: List<OutputLine>
) : ResultTreeNode(project, parentNode)