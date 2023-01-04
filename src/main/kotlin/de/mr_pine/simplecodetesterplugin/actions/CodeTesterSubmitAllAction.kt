package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import de.mr_pine.simplecodetesterplugin.CodeTester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CodeTesterSubmitAllAction(text: String): NotificationAction(text) {

    @Suppress("unused")
    constructor(): this("")
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent, notification: Notification) = actionPerformed(e)
    override fun actionPerformed(event: AnActionEvent) {
        println("Submitting")
        event.project?.let { project ->
            val sourceRoot = ModuleRootManager.getInstance(ModuleManager.getInstance(project).modules[0]).sourceRoots[0]
            val fileList = mutableListOf<VirtualFile>()
            VfsUtilCore.iterateChildrenRecursively(
                sourceRoot,
                { true },
                { file ->
                    if (!file.isDirectory && (file.extension ?: "") == "java") fileList.add(file)
                    true
                }
            )

            CoroutineScope(Job() + Dispatchers.IO).launch {
                val category = CodeTester.currentCategory
                if (category != null) {
                    val result = CodeTester.submitFiles(category = category, files = fileList)
                    println(result.first())
                } else {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("codetester.notifications")
                        .createNotification("You have to select a category", NotificationType.ERROR)
                        .setTitle("No category selected")
                        .addAction(CodeTesterCategorySelectionNotificationAction("Select Category"))
                        .addAction(CodeTesterSubmitAllAction("Retry"))
                        .notify(project)
                }
            }

            LOG.info(fileList.toString())
        }
    }

    private val icon = AllIcons.Actions.Upload
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
        event.presentation.icon = icon
    }

    companion object {
        private val LOG = Logger.getInstance(CodeTesterSubmitAllAction::class.java).apply {
            setLevel(LogLevel.DEBUG)
        }
    }
}