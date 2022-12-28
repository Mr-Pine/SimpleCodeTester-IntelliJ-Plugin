package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import de.mr_pine.simplecodetesterplugin.CodeTester
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterLoginDialogWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeTesterLoginAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialogWrapper = CodeTesterLoginDialogWrapper()

        if (dialogWrapper.showAndGet()) {
            CoroutineScope(Dispatchers.IO).launch {
                CodeTester.login(username = dialogWrapper.username, password = dialogWrapper.password)
            }
        }
    }
}