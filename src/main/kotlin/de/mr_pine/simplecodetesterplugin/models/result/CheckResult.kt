package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

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