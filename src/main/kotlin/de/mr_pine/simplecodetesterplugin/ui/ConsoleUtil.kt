package de.mr_pine.simplecodetesterplugin.ui

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import de.mr_pine.simplecodetesterplugin.models.result.CompilationOutput
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

fun ConsoleView.print(compilationOutput: CompilationOutput) {
    val contentType =
        if (compilationOutput.successful) ConsoleViewContentType.LOG_VERBOSE_OUTPUT
        else ConsoleViewContentType.LOG_ERROR_OUTPUT

    val mainText = if (compilationOutput.successful)
        "Compilation successful\n"
    else
        "Compilation not successful:\n${compilationOutput.diagnostics.toDiagnosticString()}"

    println(mainText, contentType)
    println(compilationOutput.output, contentType)
}

private fun Map<String, List<String>>.toDiagnosticString() =
    this.entries.joinToString("\n\n") {
        val valueString = it.value.joinToString("\n") { it.split("\n").joinToString("\n\t") }
        "${it.key}\n\t$valueString"
    }


fun ConsoleView.println(text: String, contentType: ConsoleViewContentType) = print("${text}\n", contentType)