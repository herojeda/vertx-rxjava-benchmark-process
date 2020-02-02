package com.ragnaroft.process_benchmarks.util.extension

import io.vertx.reactivex.ext.web.client.HttpRequest

fun <T> HttpRequest<T>.addQueryParams(params: Map<String, String>): HttpRequest<T> {
    params.forEach {
        this.addQueryParam(it.key, it.value)
    }
    return this
}

fun <T> HttpRequest<T>.putHeaders(headers: Map<String, String>): HttpRequest<T> {
    headers.forEach {
        this.putHeader(it.key, it.value)
    }
    return this
}
