package de.mr_pine.simplecodetesterplugin.models.result.tree

import com.intellij.openapi.project.Project
import de.mr_pine.simplecodetesterplugin.models.result.CheckResult
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.FileResultNode
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.ResultTreeNode
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.CheckResultNode
import kotlin.time.Duration.Companion.milliseconds

fun CodeTesterResult.tree(project: Project): ResultTreeNode {
    val rootNode = ResultTreeNode(project, null)
    val resultTreeNode = ResultTreeNode(project, rootNode, duration)
    rootNode.add(resultTreeNode)

    this.fileResults?.forEach {
        val fileResultNode = FileResultNode(project, resultTreeNode, it.key)
        it.value.forEach { checkResult ->
            val checkResultNode = CheckResultNode(project, fileResultNode, checkResult.check, checkResult.durationMillis.milliseconds, checkResult.result == CheckResult.Result.SUCCESSFUL)
            fileResultNode.add(checkResultNode)
        }
        resultTreeNode.add(fileResultNode)
    }

    return rootNode
}