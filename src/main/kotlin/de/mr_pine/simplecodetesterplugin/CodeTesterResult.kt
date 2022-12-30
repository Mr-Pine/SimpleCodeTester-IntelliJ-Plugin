package de.mr_pine.simplecodetesterplugin

import kotlinx.serialization.Serializable

@Serializable
data class CodeTesterResult(
    val fileResults: Map<String, List<CheckResult>>? = null,
    val compilationOutput: CompilationOutput
)

@Serializable
data class CompilationOutput(
    val diagnostics: Map<String, List<String>>,
    val successful: Boolean,
    val output: String
)

@Serializable
data class CheckResult(
    val check: String,
    val result: Result,
    val message: String,
    val output: List<OutputLine>,
    val errorOutput: String,
    val files: List<TestFile>,
    val durationMillis: Int
) {
    @Suppress("unused")
    enum class Result {
        SUCCESSFUL, FAILED, NOT_APPLICABLE
    }
}

@Serializable
data class OutputLine(
    val type: OutputType,
    val content: String
) {
    @Suppress("unused")
    enum class OutputType {
        PARAMETER, ERROR, INPUT, OUTPUT, OTHER
    }
}

@Serializable
data class TestFile(
    val name: String,
    val content: String
)