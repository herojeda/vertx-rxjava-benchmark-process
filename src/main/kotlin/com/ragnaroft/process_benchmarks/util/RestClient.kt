package com.ragnaroft.process_benchmarks.util

import com.ragnaroft.process_benchmarks.configuration.RestClientConfig
import com.ragnaroft.process_benchmarks.configuration.SystemConfig
import com.ragnaroft.process_benchmarks.util.extension.LoggerDelegate
import com.ragnaroft.process_benchmarks.exception.base.ExceptionResolver
import com.ragnaroft.process_benchmarks.exception.repository.rest.InternalServerClientErrorException
import com.ragnaroft.process_benchmarks.util.extension.addQueryParams
import com.ragnaroft.process_benchmarks.util.extension.putHeaders
import io.reactivex.Single
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RestClient @Inject constructor(
    private val systemConfig: SystemConfig,
    private val restClientConfig: RestClientConfig,
    private val client: WebClient
) {

    companion object {
        const val APP_ID_HEADER = "x_application_id"
    }

    private val logger by LoggerDelegate()

    private val defaultResponseHandler: (HttpResponse<Buffer>, String) -> HttpResponse<Buffer> =
        { res: HttpResponse<Buffer>, path: String ->
            handle4xx(res, path)
            handle5xx(res, path)
            handle2xx(res)
        }

    class WithoutBody

    fun withoutBody() =
        WithoutBody()

    fun <T> handle2xx(res: HttpResponse<T>) = res

    fun <T> handle4xx(res: HttpResponse<T>, path: String) {
        if (res.statusCode() in 400..499) {
            logger.error(
                "ERROR_4XX {} with message {} and body {}",
                path,
                res.statusMessage(),
                res.bodyAsString()
            )
            throw ExceptionResolver.resolve4xx(res.statusCode(), res.bodyAsString() ?: "RESPONSE_ERROR_WITHOUT_BODY")
        }
    }

    fun <T> handle5xx(res: HttpResponse<T>, path: String) {
        if (res.statusCode() in 500..599) {
            logger.error("ERROR_5XX to {} with message {}", path, res.statusMessage())
            throw InternalServerClientErrorException(res.statusMessage())
        }
    }

    fun <T> post(
        path: String,
        body: T,
        host: String? = null,
        country: String = "",
        queryParams: Map<String, String> = mapOf(),
        headers: Map<String, String> = mapOf(),
        timeout: Long = this.restClientConfig.defaultTimeout,
        responseHandler: ((HttpResponse<Buffer>, String) -> HttpResponse<Buffer>) = defaultResponseHandler
    ): Single<HttpResponse<Buffer>> {
        val basePath =
            host ?: restClientConfig.hosts[country] ?: throw InternalServerClientErrorException("COUNTRY_NOT_FOUND")
        val url = basePath + path
        val req = client.postAbs(url)
            .addQueryParams(queryParams)
            .putHeaders(headers)
            .putHeader(APP_ID_HEADER, this.systemConfig.xApplicationId)
        val httpResponse = if (body == withoutBody()) req.rxSend() else req.rxSendJson(body)

        logger.info("NEW_REQUEST_POST $url")

        return httpResponse
            .timeout(timeout, TimeUnit.MILLISECONDS)
            .map { responseHandler(it, url) }
            .doOnError { error ->
                logger.error("ERROR_REQUEST_EXCEPTION", error)
                throw error
            }
    }

    fun get(
        path: String,
        host: String? = null,
        country: String = "",
        queryParams: Map<String, String> = mapOf(),
        headers: Map<String, String> = mapOf(),
        timeout: Long = this.restClientConfig.defaultTimeout,
        responseHandler: ((HttpResponse<Buffer>, String) -> HttpResponse<Buffer>) = defaultResponseHandler
    ): Single<HttpResponse<Buffer>> {
        val basePath =
            host ?: restClientConfig.hosts[country] ?: throw InternalServerClientErrorException("COUNTRY_NOT_FOUND")
        val url = basePath + path
        val req = client.getAbs(url)
            .addQueryParams(queryParams)
            .putHeaders(headers)
            .putHeader(APP_ID_HEADER, this.systemConfig.xApplicationId)

        logger.info("NEW_REQUEST_GET $url")


        return req.rxSend().timeout(timeout, TimeUnit.MILLISECONDS)
            .map { responseHandler(it, url) }
            .doOnError { error ->
                logger.error("ERROR_REQUEST_EXCEPTION", error)
                throw error
            }
    }

    fun internalHeaders(): Map<String, String> {
        return mapOf(
            "AUTH_OWNER" to "123",
            "AUTH_USER" to "123",
            "AUTH_SCOPE" to "all"
        )
    }

    fun defaultHost(): String = restClientConfig.hosts[systemConfig.appCountryCode] ?: throw InternalServerClientErrorException("COUNTRY_NOT_FOUND")
}
