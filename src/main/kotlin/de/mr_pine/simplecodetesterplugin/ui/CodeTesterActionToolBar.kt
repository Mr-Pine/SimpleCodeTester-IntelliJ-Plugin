package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import de.mr_pine.simplecodetesterplugin.actions.CategorySelectionComboBoxAction
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterGetCategoriesAction
import org.jetbrains.annotations.NonNls
import javax.swing.Box

class CodeTesterActionToolBar(orientation: ToolBarOrientation, place: @NonNls String = "???") {
    private val actionGroup = ActionManager.getInstance().getAction("de.mr_pine.simplecodetesterplugin.actions.CodeTesterToolbarActions") as ActionGroup

    private val toolbar = ActionManager.getInstance().createActionToolbar(place, actionGroup, orientation.value)

    val component
        get() = Box.createHorizontalBox().apply { add(toolbar.component) }
}

enum class ToolBarOrientation(val value: Boolean) {
    HORIZONTAL(true), VERTICAL(false)
}