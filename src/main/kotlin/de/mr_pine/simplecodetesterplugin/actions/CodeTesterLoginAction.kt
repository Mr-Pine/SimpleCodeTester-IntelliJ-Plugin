package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import de.mr_pine.simplecodetesterplugin.CodeTester
import de.mr_pine.simplecodetesterplugin.ui.CodeTesterLoginDialogWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CodeTesterLoginAction(text: String) : NotificationAction(text) {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    constructor() : this("")

    override fun actionPerformed(e: AnActionEvent, notification: Notification) = actionPerformed(e)
    override fun actionPerformed(e: AnActionEvent) {
        val dialogWrapper = CodeTesterLoginDialogWrapper()

        if (dialogWrapper.showAndGet()) {
            CoroutineScope(Job() + Dispatchers.IO).launch {
                CodeTester.login(username = dialogWrapper.username, password = dialogWrapper.password)
            }
        }
    }

}