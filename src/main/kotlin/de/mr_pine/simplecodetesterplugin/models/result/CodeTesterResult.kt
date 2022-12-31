package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class CodeTesterResult(
    val fileResults: Map<String, List<CheckResult>>? = null,
    val compilationOutput: CompilationOutput
) {
    @Transient
    var duration: Duration = (-1).seconds
}