package com.ragnaroft.process_benchmarks.entrypoint.router.base

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response
import com.ragnaroft.process_benchmarks.exception.base.RequestValidationException
import io.vertx.core.json.Json
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext

interface BaseRouter {

    fun getPath(): String

    fun create(): Router

    fun <T> mapRequest(ctx: RoutingContext, type: Class<T>): T {
        try {
            return ctx.bodyAsJson.mapTo(type)
        } catch (e: Exception) {
            throw RequestValidationException(ctx.bodyAsString)
        }
    }

    fun completeResponseJson(ctx: RoutingContext, response: Any) {
        ctx.response()
            .putHeader("content-type", "$CONTENT_TYPE_JSON; charset=utf-8")
            .end(Json.encode(response))
    }

    companion object {
        const val CONTENT_TYPE_JSON: String = "application/json"
    }
}
