package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration

@Serializable
data class CodeTesterResult(
    val fileResults: Map<String, List<CheckResult>>? = null,
    val compilationOutput: CompilationOutput,
    val timeoutData: TimeoutData? = null
) {
    @Transient
    var duration: Duration? = null
}