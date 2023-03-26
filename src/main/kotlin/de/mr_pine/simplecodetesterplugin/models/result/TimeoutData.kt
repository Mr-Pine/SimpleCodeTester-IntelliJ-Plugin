package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

@Serializable
data class TimeoutData(
    val lastTest: String
)