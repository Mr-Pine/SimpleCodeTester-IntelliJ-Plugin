package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

@Serializable
data class OutputLine(
    val content: String,
    val type: OutputType
) {
    @Suppress("unused")
    enum class OutputType {
        PARAMETER, ERROR, INPUT, OUTPUT, OTHER
    }
}