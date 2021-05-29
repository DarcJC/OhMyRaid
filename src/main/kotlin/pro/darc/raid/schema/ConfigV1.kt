package pro.darc.raid.schema

import kotlinx.serialization.Serializable

@Serializable
data class ConfigV1(
    val default_locale: String = "zh-Hans",
)
