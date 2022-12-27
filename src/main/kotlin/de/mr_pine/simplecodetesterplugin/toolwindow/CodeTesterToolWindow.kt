package de.mr_pine.simplecodetesterplugin.toolwindow

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.text.HtmlChunk.span
import com.intellij.openapi.util.text.HtmlChunk.text
import com.intellij.ui.Colors
import com.intellij.ui.components.noteComponent
import com.intellij.ui.dsl.builder.panel

class CodeTesterToolWindow {
}

fun createDialogPanel(): DialogPanel = panel {
    row {
        this.comment("hi")
    }
}