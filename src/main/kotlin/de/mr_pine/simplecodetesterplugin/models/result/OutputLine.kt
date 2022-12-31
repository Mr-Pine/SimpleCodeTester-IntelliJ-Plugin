package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

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