package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import de.mr_pine.simplecodetesterplugin.models.result.OutputLine

fun ConsoleView.print(lines: List<OutputLine>) = lines.forEach {
    val contentType = when (it.type) {
        OutputLine.OutputType.PARAMETER -> ConsoleViewContentType.LOG_INFO_OUTPUT
        OutputLine.OutputType.ERROR -> ConsoleViewContentType.ERROR_OUTPUT
        OutputLine.OutputType.INPUT -> ConsoleViewContentType.USER_INPUT
        OutputLine.OutputType.OUTPUT -> ConsoleViewContentType.NORMAL_OUTPUT
        OutputLine.OutputType.OTHER -> ConsoleViewContentType.LOG_VERBOSE_OUTPUT
    }
    println(it.content, contentType)
}

fun ConsoleView.println(text: String, contentType: ConsoleViewContentType) = print("${text}\n", contentType)