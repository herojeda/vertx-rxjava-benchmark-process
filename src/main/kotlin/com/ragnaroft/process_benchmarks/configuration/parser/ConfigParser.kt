package com.ragnaroft.process_benchmarks.configuration.parser

import io.reactivex.Single
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.config.ConfigRetriever
import io.vertx.reactivex.core.Vertx
import java.util.regex.Pattern

/**
 * Application-wide configuration parser
 * <p>This class provides resolves the application configuration and returns a JsonObject with the results.</p>
 * <p>Configuration is loaded from resource files located in the <i>conf</i> folder</p>
 * <ul>
 *     <li> <i>conf/default.yaml</i> provides default values for all the locations and environments </li>
 *     <li> <i>conf/local-dev.yaml</i> provides configuration overrides used when executing locally </li>
 *     <li> <i>conf/server.yaml</i> provides configuration overrides used when executing on a server </li>
 *     <li> <i>conf/server-<ENV>.yaml provides configuration overrides used when executing on a given environment <li>
 * </ul>
 * <p>The final config is the result of merging the default, location and environment config files. When a value is
 * defined in more than one file, the last one takes precedence </p>
 * <p> Additionally, properties may reference environment variables using the syntax ${var}. This will be resolved at
 * runtime to the correct value, or fail if there's no environment variable defined </p>
 *
 * <p> While not mandatory, it is suggested that you consider this structure for your config files: </p>
 * <pre>
 *     storage:       // group for all the data sources
 *       database:
 *         url: xx
 *         user: xx
 *         pass: xx
 *       redis:
 *         host: xx
 *         port: xx
 *         database: xx
 *     services:     // group for all the external apis
 *       external-service-1:
 *         url: xxx
 *         client-id: xxx
 *         client-secret: xxx
 *         bar: yyy
 *         foo: yyy
 *       external-service-2:
 *         url: xxx
 *         bar: yyy
 *         foo: yyy
 *       rappi:  // url (and headers/tokens) needed to call other internal services
 *         url: xxx
 *     config:
 *       section1:  // keeping a named section for each piece of config (to make it easy to convert it to an object
 *         foo: xx
 *         bar:
 *           prop1: xx
 *           prop2: xx
 *       section2:
 *         foo: xx
 *         bar: yy
 * </pre>
 *
 *
 * <p>Location and environment are defined based on two environment variables SCOPE and ENV. If SCOPE is defined,
 * the location is considered to be server, if not, local. If ENV is defined, it defines the environment name.
 *
 * </p>
 */
class ConfigParser(private val vertx: Vertx, base: String = "conf") {

    companion object {
        private val envReader =  DefaultEnvReader()
        fun resolveLocation(): String = if (envReader.getEnv("SCOPE") == null) "local" else "server"
    }
    private val envPattern = Pattern.compile("\\$\\{(.*?)}")
    private val basePath = base


    fun read(): Single<JsonObject> {

        val baseConfig = configStore( "default.yaml")

        val loc = resolveLocation()
        val locOverride = configStore("$loc.yaml").setOptional(true)

        val env = resolveEnvironment()
        val envOverride = configStore("$loc-$env.yaml").setOptional(true)

        val options = ConfigRetrieverOptions()
            .addStore(baseConfig)
            .addStore(locOverride)
            .addStore(envOverride)

        val retriever = ConfigRetriever.create(vertx, options)

        return retriever.rxGetConfig()
            .doOnSuccess { it.replaceEnvVars() }
    }

    private fun configStore(fileName: String): ConfigStoreOptions {
        return ConfigStoreOptions().apply {
            type = "file"
            format = "yaml"
            config = JsonObject().put("path", "$basePath/$fileName")
        }
    }

    private fun resolveEnvironment(): String = guard(envReader.getEnv("ENV") ?: "undefined")

    private fun guard(s: String): String {
        if (!s.matches(Regex("[a-zA-Z]+"))) {
            throw IllegalArgumentException("Invalid environment value '$s'. Only letters are accepted.")
        }
        return s
    }

    private fun JsonObject.replaceEnvVars() {
        this.fieldNames().forEach { name ->
            val v = this.getValue(name)
            when (v) {
                is String -> this.put(name, v.resolvedEnvVars())
                is JsonObject -> v.replaceEnvVars()
                is JsonArray -> v.replaceEnvVars()
            }
        }
    }

    private fun JsonArray.replaceEnvVars() {
        for ((index, v) in this.withIndex()) {
            when(v) {
                is String -> this.list[index] = v.resolvedEnvVars()
                is JsonObject -> v.replaceEnvVars()
                is JsonArray -> v.replaceEnvVars()
            }
        }
    }

    // replace ${xxx} with env var xxx
    private fun String.resolvedEnvVars(): String {
        var previousStart = 0
        val sb = StringBuilder()

        val matcher = envPattern.matcher(this)
        while (matcher.find()) {
            val match = matcher.group(1)
            val start = matcher.start()
            val end = matcher.end()

            val resolved = envReader.getEnv(match)
                ?: throw IllegalStateException("Environment var $match needed as config was not defined.")

            sb.append(this.substring(previousStart, start))
            sb.append(resolved)

            previousStart = end
        }

        if (previousStart == 0) {
            return this
        }
        if (previousStart < this.length) {
            sb.append(this.substring(previousStart))
        }
        return sb.toString()
    }

    interface EnvReader {
        fun getEnv(name: String): String?
    }

    private class DefaultEnvReader: EnvReader {
        override fun getEnv(name: String): String? = System.getenv(name)
    }
}
