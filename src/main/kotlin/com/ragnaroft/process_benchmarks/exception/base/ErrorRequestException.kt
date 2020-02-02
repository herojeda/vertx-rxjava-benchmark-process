package com.ragnaroft.process_benchmarks.exception.base

import com.ragnaroft.process_benchmarks.entrypoint.dto.response.Response

abstract class ErrorRequestException(
    override var message: String,
    open var errorCode: String,
    var statusCode: Int
) : RuntimeException(message) {
    abstract fun getErrorResponse(): Response<out Any>
}
