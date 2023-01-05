package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SideBorder
import com.intellij.ui.render.RenderingHelper
import com.intellij.ui.tree.AsyncTreeModel
import com.intellij.ui.tree.StructureTreeModel
import com.intellij.ui.tree.ui.DefaultTreeUI
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EditSourceOnDoubleClickHandler
import com.intellij.util.EditSourceOnEnterKeyHandler
import com.intellij.util.concurrency.Invoker
import com.intellij.util.ui.tree.TreeUtil
import de.mr_pine.simplecodetesterplugin.models.result.TestCategory
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.models.result.tree.CodeTesterResultTreeStructure
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.CheckResultNode
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.CodeTesterNodeRenderer
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.RootResultNode
import de.mr_pine.simplecodetesterplugin.models.result.tree.tree
import kotlinx.coroutines.flow.Flow
import java.awt.BorderLayout
import java.awt.CardLayout
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class CodeTesterResultPanel(val project: Project, resultFlow: Flow<CodeTesterResult>, testCategory: TestCategory) : ComponentContainer {

    private val panel = JPanel(BorderLayout())
    private var tree: Tree

    init {
        val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

        val rootNode = resultFlow.tree(project, testCategory)

        val treeStructure = CodeTesterResultTreeStructure(rootNode)
        val treeModel = StructureTreeModel(treeStructure, null, Invoker.forBackgroundThreadWithReadAction(this), this)
        val asyncTreeModel = AsyncTreeModel(treeModel, this)
        tree = initTree(asyncTreeModel)

        tree.addTreeSelectionListener {
            console.clear()
            when(val node = (it.path.lastPathComponent as DefaultMutableTreeNode).userObject) {
                is CheckResultNode -> console.print(node.content)
                is RootResultNode -> node.compilationOutput?.let { output -> console.print(output) }
            }
        }

        val scrollPane = ScrollPaneFactory.createScrollPane(tree, SideBorder.NONE)

        val splitter = OnePixelSplitter(0.33f).apply {
            firstComponent = JPanel().apply {
                layout = CardLayout()
                add(scrollPane)
            }
            secondComponent = console.component
        }

        val codeTesterPanel = CodeTesterToolWindowPanel(
            topComponent = CodeTesterActionToolBar(ToolBarOrientation.HORIZONTAL).apply { setTargetComponent(splitter) }.component,
            mainComponent = splitter
        )

        (rootNode.children[0] as RootResultNode).registerFinishListener {
            treeModel.invalidateAsync()
            rootNode.children.forEach {
                it.children.forEach {
                    treeModel.expand(it, tree) {
                        println("Expanded: $it")
                    }
                }
            }
        }

        panel.add(codeTesterPanel.getPanel())
        panel.isVisible = true
    }

    private fun initTree(model: AsyncTreeModel): Tree {
        val initTree = Tree(model).apply {
            isLargeModel = true
            putClientProperty(AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED, true)
            putClientProperty(DefaultTreeUI.AUTO_EXPAND_ALLOWED, false)
            putClientProperty(RenderingHelper.SHRINK_LONG_RENDERER, true)
            isRootVisible = false
            cellRenderer = CodeTesterNodeRenderer()
        }
        TreeUtil.installActions(initTree)
        EditSourceOnDoubleClickHandler.install(initTree)
        EditSourceOnEnterKeyHandler.install(initTree)
        return initTree
    }

    private var disposed = AtomicBoolean(false)
    override fun dispose() {
        disposed.set(true)
    }

    override fun getComponent(): JComponent = panel

    override fun getPreferredFocusableComponent(): JComponent =
        tree

}