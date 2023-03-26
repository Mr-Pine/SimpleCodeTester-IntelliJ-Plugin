package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import org.jetbrains.annotations.NonNls
import javax.swing.Box

class CodeTesterActionToolBar(orientation: ToolBarOrientation, place: @NonNls String = "???") {
    private val actionGroup = ActionManager.getInstance()
        .getAction("de.mr_pine.simplecodetesterplugin.actions.CodeTesterToolbarActions") as ActionGroup

    private val toolbar = ActionManager.getInstance().createActionToolbar(place, actionGroup, orientation.value)

    val setTargetComponent = toolbar::setTargetComponent

    val component
        get() = Box.createHorizontalBox().apply { add(toolbar.component) }
}

@Suppress("unused")
enum class ToolBarOrientation(val value: Boolean) {
    HORIZONTAL(true), VERTICAL(false)
}