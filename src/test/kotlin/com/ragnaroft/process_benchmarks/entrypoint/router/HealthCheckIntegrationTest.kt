package com.ragnaroft.process_benchmarks.entrypoint.router

import com.ragnaroft.process_benchmarks.entrypoint.router.base.route.Routes.Companion.HEALTH
import com.ragnaroft.process_benchmarks.util.BaseIntegrationTest
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HealthCheckIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `HealthCheck test`(testContext: VertxTestContext) {
        val expectedResponse = mapOf(
            "status" to "OK"
        )

        restClient.get(
            path = HEALTH,
            host = baseURL
        ).subscribe(
            { result ->
                testContext.verify {
                    val jsonObject: JsonObject = (result.bodyAsJsonObject()?: JsonObject())
                    Assertions.assertEquals(jsonObject.getString("status"), "OK")
                    testContext.completeNow()
                }
            }, testContext::failNow
        )
    }

}