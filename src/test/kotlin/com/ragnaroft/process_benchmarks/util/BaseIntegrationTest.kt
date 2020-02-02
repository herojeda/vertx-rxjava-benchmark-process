package com.ragnaroft.process_benchmarks.util

import com.google.inject.Guice
import com.google.inject.Injector
import com.ragnaroft.process_benchmarks.configuration.SystemConfig
import com.ragnaroft.process_benchmarks.configuration.json.JsonMapperConfig
import com.ragnaroft.process_benchmarks.configuration.module.MainModule
import com.ragnaroft.process_benchmarks.configuration.parser.ConfigParser
import com.ragnaroft.process_benchmarks.configuration.verticle.MainVerticle
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
abstract class BaseIntegrationTest {

    companion object {
        lateinit var injector: Injector
        lateinit var webClient: WebClient
        lateinit var restClient: RestClient
        lateinit var baseURL: String

        @BeforeAll
        @JvmStatic
        fun deployApp(vertx: Vertx, testContext: VertxTestContext) {
            JsonMapperConfig.applyConfig()
            val config = ConfigParser(vertx).read().blockingGet()
            injector = Guice.createInjector(MainModule(vertx, config))

            val serverPath = injector.getInstance(SystemConfig::class.java).basePath
            val serverPort = "8080"

            baseURL = "http://localhost:$serverPort$serverPath"

            val main = injector.getInstance(MainVerticle::class.java)
            webClient = injector.getInstance(WebClient::class.java)
            restClient = injector.getInstance(RestClient::class.java)

            vertx.rxDeployVerticle(main)
                .subscribe({ testContext.completeNow() }, testContext::failNow)

            testContext.awaitCompletion(4, TimeUnit.SECONDS)
        }

        @AfterAll
        @JvmStatic
        fun undeployApp(vertx: Vertx, testContext: VertxTestContext) {
            vertx.close(testContext.succeeding { testContext.completeNow() })
        }
    }

}
