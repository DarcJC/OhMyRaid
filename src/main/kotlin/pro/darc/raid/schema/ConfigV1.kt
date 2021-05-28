package pro.darc.raid.schema

import kotlinx.serialization.Serializable

@Serializable
data class ConfigV1(
    val version: Int = 1,
)
