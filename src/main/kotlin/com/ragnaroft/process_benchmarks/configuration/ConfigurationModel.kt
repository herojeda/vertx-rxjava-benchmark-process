package com.ragnaroft.process_benchmarks.configuration

data class ConfigurationModel (
    val system: SystemConfig,
    val restClient: RestClientConfig
)

data class RestClientConfig(
    val hosts: Map<String, String>,
    val defaultTimeout: Long
)

data class SystemConfig(
    val xApplicationId: String,
    val httpPort: Int,
    val basePath: String,
    val appCountryCode: String
)