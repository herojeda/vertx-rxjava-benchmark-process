package com.ragnaroft.process_benchmarks.exception.base

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response
import io.netty.handler.codec.http.HttpResponseStatus

open class ForbiddenException(
    override var message: String,
    override var errorCode: String = HttpResponseStatus.FORBIDDEN.reasonPhrase()
) : ErrorRequestException(message, errorCode, statusCode = HttpResponseStatus.FORBIDDEN.code()) {

    override fun getErrorResponse() = Response(
        status = statusCode,
        message = errorCode,
        data = errorCode
    )

}
