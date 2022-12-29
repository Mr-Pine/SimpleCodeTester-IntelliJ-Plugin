package de.mr_pine.simplecodetesterplugin

import kotlinx.serialization.Serializable

@Serializable
data class TestCategory(
    val id: Int,
    val name: String
)