package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import de.mr_pine.simplecodetesterplugin.CodeTester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CodeTesterGetCategoriesAction: DumbAwareAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            CodeTester.getCategories()
        }
    }

    private val icon = AllIcons.Actions.Refresh
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = CodeTester.loggedIn
        event.presentation.icon = icon
    }
}