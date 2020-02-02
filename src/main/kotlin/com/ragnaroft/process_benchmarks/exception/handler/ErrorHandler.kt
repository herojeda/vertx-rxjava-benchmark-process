package com.ragnaroft.process_benchmarks.exception.handler

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response
import com.ragnaroft.process_benchmarks.exception.base.ErrorRequestException
import com.ragnaroft.process_benchmarks.util.extension.LoggerDelegate
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.exceptions.CompositeException
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.reactivex.ext.web.RoutingContext

class ErrorHandler : Handler<RoutingContext> {

    private val logger by LoggerDelegate()

    override fun handle(event: RoutingContext) {
        val response = event.response()
        val failure = event.failure()
        val cause = if (failure is CompositeException) failure.cause.cause else failure
        val bodyError: Response<out Any> = if (cause is ErrorRequestException) cause.getErrorResponse() else getGenericError()

        response.setStatusCode(bodyError.status)
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
            .end(Json.encode(bodyError))
    }

    private fun getGenericError() = Response(
        status = HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
        data = "Internal server error",
        message = "Internal server error"
    )

}
