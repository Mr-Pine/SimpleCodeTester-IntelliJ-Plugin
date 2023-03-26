package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

@Serializable
data class CompilationOutput(
    val diagnostics: Map<String, List<String>>,
    val successful: Boolean,
    val output: String,
    val files: List<String>
) {
    val outputLines: List<OutputLine>
        get() {
            val contentType = if (successful) OutputLine.OutputType.OTHER
            else OutputLine.OutputType.ERROR

            val mainText = if (successful)
                "Compilation successful\n"
            else
                "Compilation not successful:\n${diagnostics.toDiagnosticString()}"

            return listOf(
                OutputLine(mainText, contentType),
                OutputLine(output, contentType)
            )
        }

    private fun Map<String, List<String>>.toDiagnosticString() =
        this.entries.joinToString("\n\n") {
            val valueString = it.value.joinToString("\n") { it.split("\n").joinToString("\n\t") }
            "${it.key}\n\t$valueString"
        }
}