package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.Serializable

@Serializable
data class TestCategory(
    val id: Int,
    val name: String
)