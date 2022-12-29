package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import de.mr_pine.simplecodetesterplugin.CodeTester
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterLoginDialogWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CodeTesterLoginAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialogWrapper = CodeTesterLoginDialogWrapper()

        if (dialogWrapper.showAndGet()) {
            CoroutineScope(Job() + Dispatchers.IO).launch {
                CodeTester.login(username = dialogWrapper.username, password = dialogWrapper.password)
            }
        }
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        //event.presentation.icon = icon
    }
}