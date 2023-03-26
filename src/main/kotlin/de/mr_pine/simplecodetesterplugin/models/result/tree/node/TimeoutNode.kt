package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project
import javax.swing.Icon
import kotlin.time.Duration

open class TimeoutNode(project: Project, parentNode: ResultTreeNode?, val lastRunTest: String) :
    ResultTreeNode(project, parentNode, Duration.ZERO) {
    override val hint = lastRunTest
    override val title = "Timeout"
    override val success = false

    override fun getCurrentIcon(): Icon = NODE_ICON_TIMEOUT
}