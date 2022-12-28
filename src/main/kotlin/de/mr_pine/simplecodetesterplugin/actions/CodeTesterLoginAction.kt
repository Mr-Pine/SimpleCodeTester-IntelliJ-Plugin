package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterLoginDialogWrapper

class CodeTesterLoginAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        CodeTesterLoginDialogWrapper().showAndGet()
    }
}