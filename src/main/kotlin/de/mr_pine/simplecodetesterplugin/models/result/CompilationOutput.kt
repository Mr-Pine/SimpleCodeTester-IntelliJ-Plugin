package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

@Serializable
data class CompilationOutput(
    val diagnostics: Map<String, List<String>>,
    val successful: Boolean,
    val output: String,
    val files: List<String>
)