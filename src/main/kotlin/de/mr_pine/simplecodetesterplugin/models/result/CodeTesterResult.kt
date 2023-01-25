package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Transient
import kotlin.time.Duration

data class CodeTesterResult(
    val fileResults: Map<String, List<CheckResult>>? = null,
    val compilationOutput: CompilationOutput
) {
    @Transient
    var duration: Duration? = null
}