package com.ragnaroft.process_benchmarks.exception.base

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response
import io.netty.handler.codec.http.HttpResponseStatus

open class BadRequestException(
    override var message: String,
    override var errorCode: String = HttpResponseStatus.BAD_REQUEST.reasonPhrase()
) : ErrorRequestException(message, errorCode, statusCode = HttpResponseStatus.BAD_REQUEST.code()) {

    override fun getErrorResponse() = Response(
        status = statusCode,
        message = errorCode,
        data = errorCode
    )

}
