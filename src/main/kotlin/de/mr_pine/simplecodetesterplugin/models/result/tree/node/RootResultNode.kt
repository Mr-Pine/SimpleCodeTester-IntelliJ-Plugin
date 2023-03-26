package de.mr_pine.simplecodetesterplugin.models.result.tree.node

import com.intellij.openapi.project.Project
import de.mr_pine.simplecodetesterplugin.models.result.CompilationOutput
import de.mr_pine.simplecodetesterplugin.models.result.OutputLine

class RootResultNode(project: Project, parentNode: ResultTreeNode?, override val title: String) :
    ResultTreeNode(project, parentNode) {
    private var finished = false
    var compilationOutput: CompilationOutput? = null
    var errorMessage: String? = null
    override val success: Boolean
        get() = errorMessage == null && (compilationOutput?.successful ?: true) && children.all { it.success }

    fun finish() {
        finished = true
        finishListeners.forEach { it() }
    }

    private val finishListeners: MutableList<() -> Unit> = mutableListOf()
    fun registerFinishListener(listener: () -> Unit): Boolean {
        return if (finished) {
            listener()
            false
        } else {
            finishListeners.add(listener)
        }
    }

    override fun getCurrentIcon() = if (finished) super.getCurrentIcon() else NODE_ICON_RUNNING

    override val output: List<OutputLine>
        get() {
            val outputList = mutableListOf<OutputLine>()
            if (errorMessage != null)
                outputList.add(OutputLine(errorMessage!!, OutputLine.OutputType.ERROR))

            if (compilationOutput != null) {
                outputList += compilationOutput!!.outputLines
            }

            return outputList
        }
}