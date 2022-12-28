package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterLoginDialogWrapper

class CodeTesterToolWindow {
}

fun createDialogPanel(): DialogPanel = panel {
    row {
        this.comment("hi")
        button("hi") {
            if (CodeTesterLoginDialogWrapper().showAndGet()) {
                // user pressed OK
            }
        }
    }
}