package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import de.mr_pine.simplecodetesterplugin.actions.CodeTesterLoginAction

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