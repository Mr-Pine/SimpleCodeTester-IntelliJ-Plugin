package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import de.mr_pine.simplecodetesterplugin.actions.CategorySelectionComboBoxAction
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterGetCategoriesAction
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterLoginAction
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterLogoutAction

fun loggedOutDialogPanel(): DialogPanel = panel {
    row {
        panel {
            row {
                label("You are not logged in to the CodeTester").align(Align.CENTER)
            }
            row {
                button("Log In") {
                    CodeTesterLoginAction().actionPerformed(
                        AnActionEvent(
                            null,
                            DataContext.EMPTY_CONTEXT,
                            ActionPlaces.UNKNOWN,
                            Presentation(),
                            ActionManager.getInstance(),
                            0
                        )
                    )
                }.align(Align.CENTER)
            }
        }.resizableColumn().align(AlignY.CENTER)
    }.resizableRow()
}

fun submitDialogPanel(): DialogPanel = panel {
    row {
        val actionGroup = DefaultActionGroup().apply { addAll(CodeTesterGetCategoriesAction(), CategorySelectionComboBoxAction()) }
        val actionToolbar = ActionManager.getInstance().createActionToolbar("ToolWindow", actionGroup, true)
        actionToolbar.targetComponent = null
        cell(actionToolbar.component)

        actionButton(CodeTesterGetCategoriesAction())
        actionsButton(CategorySelectionComboBoxAction())
        actionButton(CodeTesterLogoutAction())
    }
    row {
        panel {
            row {
                label("You are logged in to the CodeTester").align(Align.CENTER)
            }
        }.resizableColumn().align(AlignY.CENTER)
    }.resizableRow()
}