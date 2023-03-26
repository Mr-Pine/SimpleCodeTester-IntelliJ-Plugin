package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.Condition
import de.mr_pine.simplecodetesterplugin.CodeTester
import de.mr_pine.simplecodetesterplugin.models.result.TestCategory
import java.awt.Dimension
import javax.swing.JComponent

class CodeTesterCategorySelectionComboBoxAction : ComboBoxAction(), DumbAware {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    private val icon = AllIcons.Actions.ListChanges
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = CodeTester.loggedIn
        e.presentation.icon = icon
        CodeTester.currentCategory?.let { e.presentation.setText(it.name, false) }
    }

    override fun createPopupActionGroup(button: JComponent, dataContext: DataContext): DefaultActionGroup {
        val actionGroup = DefaultActionGroup()

        actionGroup.addAll(CodeTester.categories.map { SelectCategoryAction(it, it == CodeTester.currentCategory) })

        return actionGroup
    }

    override fun createActionPopup(
        group: DefaultActionGroup,
        context: DataContext,
        disposeCallback: Runnable?
    ): ListPopup {
        val popup = JBPopupFactory.getInstance().createActionGroupPopup(
            myPopupTitle,
            group,
            context,
            null,
            shouldShowDisabledActions(),
            disposeCallback,
            maxRows,
            preselectCondition,
            null
        )
        popup.setMinimumSize(Dimension(minWidth, minHeight))
        return popup
    }

    override fun getPreselectCondition() =
        Condition<AnAction> { if (it is SelectCategoryAction) it.isSelected else false }

    override fun shouldShowDisabledActions() = true

    private class SelectCategoryAction(
        private val category: TestCategory,
        val isSelected: Boolean
    ) : DumbAwareAction() {

        init {
            val name = category.name
            templatePresentation.setText(name, false)
        }

        override fun getActionUpdateThread() = ActionUpdateThread.BGT

        override fun actionPerformed(e: AnActionEvent) {
            CodeTester.currentCategory = category
        }

    }
}

class CodeTesterCategorySelectionNotificationAction(text: String) : NotificationAction(text) {

    @Suppress("unused")
    constructor() : this("")

    private val selectionAction = CodeTesterCategorySelectionComboBoxAction()
    override fun actionPerformed(e: AnActionEvent, notification: Notification) = selectionAction.actionPerformed(e)

}