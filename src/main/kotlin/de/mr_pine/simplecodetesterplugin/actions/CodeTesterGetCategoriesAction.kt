package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import de.mr_pine.simplecodetesterplugin.CodeTester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CodeTesterGetCategoriesAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            CodeTester.getCategories()
        }
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = CodeTester.loggedIn
    }
}