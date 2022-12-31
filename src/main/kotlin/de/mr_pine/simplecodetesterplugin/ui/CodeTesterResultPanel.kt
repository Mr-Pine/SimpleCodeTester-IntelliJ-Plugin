package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.IdeBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SideBorder
import com.intellij.ui.components.JBLabel
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
import de.mr_pine.simplecodetesterplugin.models.result.tree.tree
import java.awt.BorderLayout
import java.awt.CardLayout
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent
import javax.swing.JPanel

class CodeTesterResultPanel(val project: Project, result: CodeTesterResult): ComponentContainer {

    val panel = JPanel(BorderLayout())
    val tree: Tree

    init {
        val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        console.print("test\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        console.print("test input\n", ConsoleViewContentType.USER_INPUT)

        val rootNode = result.tree(project)

        val treeStructure = CodeTesterResultTreeStructure(rootNode)
        val treeModel = StructureTreeModel(treeStructure, null, Invoker.forBackgroundThreadWithReadAction(this), this)
        val asyncTreeModel = AsyncTreeModel(treeModel, this)
        tree = initTree(asyncTreeModel)

        val treePanel = JPanel().apply {
            layout = CardLayout()
            val scrollPane = ScrollPaneFactory.createScrollPane(tree, SideBorder.NONE)
            add(scrollPane)
        }

        val splitter = OnePixelSplitter(0.33f).apply {
            firstComponent = treePanel
            secondComponent = console.component
        }

        val toolWindowPanel = CodeTesterToolWindowPanel(
            this::class.java,
            topComponent = CodeTesterActionToolBar(ToolBarOrientation.HORIZONTAL).apply { setTargetComponent(splitter) }.component,
            mainComponent = splitter
        )
        panel.add(toolWindowPanel.getPanel())
        panel.isVisible = true
    }

    private fun initTree(model: AsyncTreeModel): Tree {
        val tree = Tree(model).apply {
            isLargeModel = true
            putClientProperty(AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED, true)
            putClientProperty(DefaultTreeUI.AUTO_EXPAND_ALLOWED, false)
            putClientProperty(RenderingHelper.SHRINK_LONG_RENDERER, true)
            isRootVisible = false
            cellRenderer = CodeTesterNodeRenderer()
        }
        TreeUtil.installActions(tree)
        EditSourceOnDoubleClickHandler.install(tree)
        EditSourceOnEnterKeyHandler.install(tree)
        return tree
    }

    var disposed = AtomicBoolean(false)
    override fun dispose() {
        disposed.set(true)
    }

    override fun getComponent(): JComponent = panel

    override fun getPreferredFocusableComponent(): JComponent = tree


}