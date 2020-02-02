package com.ragnaroft.process_benchmarks.exception.base

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response
import io.netty.handler.codec.http.HttpResponseStatus

open class NotFoundException(
    override var message: String,
    override var errorCode: String = HttpResponseStatus.NOT_FOUND.reasonPhrase()
) : ErrorRequestException(message, errorCode, statusCode = HttpResponseStatus.NOT_FOUND.code()) {

    override fun getErrorResponse() = Response(
        status = statusCode,
        message = errorCode,
        data = errorCode
    )

}
