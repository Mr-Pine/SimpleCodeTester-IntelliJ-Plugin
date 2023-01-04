package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.openapi.ui.getTreePath
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
import de.mr_pine.simplecodetesterplugin.models.result.CodeTesterResult
import de.mr_pine.simplecodetesterplugin.models.result.tree.CodeTesterResultTreeStructure
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.CodeTesterNodeRenderer
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.ResultTreeNode
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.RootResultNode
import de.mr_pine.simplecodetesterplugin.models.result.tree.tree
import kotlinx.coroutines.flow.Flow
import java.awt.BorderLayout
import java.awt.CardLayout
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class CodeTesterResultPanel(val project: Project, resultFlow: Flow<CodeTesterResult>) : ComponentContainer {

    private val panel = JPanel(BorderLayout())
    private var tree: Tree

    init {
        val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        console.print("test\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        console.print("test input\n", ConsoleViewContentType.USER_INPUT)

        val rootNode = resultFlow.tree(project)

        val asyncModel = createTreeModel(rootNode)
        tree = initTree(asyncModel)

        val scrollPane = ScrollPaneFactory.createScrollPane(tree, SideBorder.NONE)

        val splitter = OnePixelSplitter(0.33f).apply {
            firstComponent = JPanel().apply {
                layout = CardLayout()
                add(scrollPane)
            }
            secondComponent = console.component
        }

        val codeTesterPanel = CodeTesterToolWindowPanel(
            this::class.java,
            topComponent = CodeTesterActionToolBar(ToolBarOrientation.HORIZONTAL).apply { setTargetComponent(splitter) }.component,
            mainComponent = splitter
        )

        (rootNode.children[0] as RootResultNode).registerFinishListener {
            tree.model = createTreeModel(rootNode)
            /*(tree.model as AsyncTreeModel).onValidThread {
                val path = TreePath(rootNode).pathByAddingChild(rootNode.children[0]).pathByAddingChild(rootNode.children[0])
                tree.expandPath(path)
                //tree.expandRow(tree.rowCount)
            }*/

        }

        panel.add(codeTesterPanel.getPanel())
        panel.isVisible = true
    }

    private fun initTree(model: TreeModel): Tree {
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

    private fun createTreeModel(rootNode: ResultTreeNode): TreeModel {
        val treeStructure = CodeTesterResultTreeStructure(rootNode)
        val treeModel = StructureTreeModel(treeStructure, null, Invoker.forBackgroundThreadWithReadAction(this), this)
        val asyncModel = AsyncTreeModel(treeModel, this)
        /*asyncModel.addTreeModelListener(object : TreeModelListener {
            override fun treeNodesChanged(e: TreeModelEvent?) {
                e?.let { tree.expandPath(it.treePath) }
            }

            override fun treeNodesInserted(e: TreeModelEvent?) {
                e?.let { tree.expandPath(it.treePath) }
            }

            override fun treeNodesRemoved(e: TreeModelEvent?) {
                e?.let { tree.expandPath(it.treePath) }
            }

            override fun treeStructureChanged(e: TreeModelEvent?) {
                e?.let { tree.expandPath(it.treePath) }
            }

        })*/
        return asyncModel
    }

    private var disposed = AtomicBoolean(false)
    override fun dispose() {
        disposed.set(true)
    }

    override fun getComponent(): JComponent = panel

    override fun getPreferredFocusableComponent(): JComponent = tree!!


}