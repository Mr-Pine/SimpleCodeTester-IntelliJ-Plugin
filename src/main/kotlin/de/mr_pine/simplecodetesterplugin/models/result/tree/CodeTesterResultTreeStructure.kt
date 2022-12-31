package de.mr_pine.simplecodetesterplugin.models.result.tree

import com.intellij.ide.util.treeView.AbstractTreeStructure
import com.intellij.ide.util.treeView.NodeDescriptor
import de.mr_pine.simplecodetesterplugin.models.result.tree.node.ResultTreeNode
import kotlinx.coroutines.repackaged.net.bytebuddy.implementation.bytecode.Throw

class CodeTesterResultTreeStructure(val root: ResultTreeNode) : AbstractTreeStructure() {
    override fun getRootElement() = root

    override fun getChildElements(element: Any): Array<Any> = if(element is ResultTreeNode) element.children.toTypedArray() else throw IllegalArgumentException("element must be ResultTreeNode")

    override fun getParentElement(element: Any): Any?  = if(element is ResultTreeNode) element.parentNode else throw IllegalArgumentException("element must be ResultTreeNode")

    override fun createDescriptor(element: Any, parentDescriptor: NodeDescriptor<*>?) = element as NodeDescriptor<*>

    override fun commit() {}

    override fun hasSomethingToCommit() = false

}