package com.ragnaroft.process_benchmarks.exception.base

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response
import io.netty.handler.codec.http.HttpResponseStatus

open class UnauthorizedException(
    override var message: String,
    override var errorCode: String = HttpResponseStatus.UNAUTHORIZED.reasonPhrase()
) : ErrorRequestException(message, errorCode, statusCode = HttpResponseStatus.UNAUTHORIZED.code()) {

    override fun getErrorResponse() = Response(
        status = statusCode,
        message = errorCode,
        data = errorCode
    )

}
