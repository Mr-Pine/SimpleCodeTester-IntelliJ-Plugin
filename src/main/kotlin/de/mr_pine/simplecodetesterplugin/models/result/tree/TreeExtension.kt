package de.mr_pine.simplecodetesterplugin.models.result.tree

import com.intellij.openapi.project.Project
import de.mr_pine.simplecodetesterplugin.models.result.CheckResult
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.models.result.TestCategory
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun Flow<Result<CodeTesterResult>>.tree(project: Project, category: TestCategory): ResultTreeNode {
    val rootNode = ResultTreeNode(project, null)
    val resultTreeNode = RootResultNode(project, rootNode, category.name)
    rootNode.add(resultTreeNode)

    CoroutineScope(Job() + Dispatchers.IO).launch {
        val resultResult = first()

        if (resultResult.isSuccess) {
            val result = resultResult.getOrThrow()

            resultTreeNode.duration = result.duration

            result.fileResults?.forEach {
                val fileResultNode = FileResultNode(project, resultTreeNode, it.key)
                it.value.forEach { checkResult ->
                    val checkResultNode = CheckResultNode(
                        project,
                        fileResultNode,
                        checkResult.check,
                        checkResult.durationMillis.milliseconds,
                        checkResult.result == CheckResult.Result.SUCCESSFUL,
                        checkResult.output
                    )
                    fileResultNode.add(checkResultNode)
                }
                resultTreeNode.add(fileResultNode)
            }

            if (result.timeoutData != null) {
                resultTreeNode.add(TimeoutNode(project, resultTreeNode, result.timeoutData.lastTest))
            }

            resultTreeNode.finish()
            resultTreeNode.compilationOutput = result.compilationOutput
            println("done")
        } else {
            resultTreeNode.errorMessage = resultResult.exceptionOrNull()!!.message
            resultTreeNode.finish()
        }
    }

    return rootNode
}