package com.ragnaroft.process_benchmarks.entrypoint.dto.response

import io.netty.handler.codec.http.HttpResponseStatus
import java.time.LocalDateTime

data class Response<T>(
    var timestamp: LocalDateTime = LocalDateTime.now(),
    var status: Int = 0,
    var message: String = "",
    var data: T
) {

    companion object {
        fun <T> responseOkWith(data: T): Response<T> = Response(
            data = data
        ).responseOK()
    }

    fun withTimestamp(timestamp: LocalDateTime): Response<T> {
        this.timestamp = timestamp
        return this
    }

    fun withStatus(status: Int): Response<T> {
        this.status = status
        return this
    }

    fun withMessage(message: String): Response<T> {
        this.message = message
        return this
    }

    fun withData(data: T): Response<T> {
        this.data = data
        return this
    }

    fun responseOK(): Response<T> {
        message = HttpResponseStatus.OK.reasonPhrase()
        status = HttpResponseStatus.OK.code()
        return this
    }

}
