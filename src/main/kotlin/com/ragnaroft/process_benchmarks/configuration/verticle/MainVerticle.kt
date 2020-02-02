package com.ragnaroft.process_benchmarks.configuration.verticle

import com.google.inject.Inject
import com.ragnaroft.process_benchmarks.configuration.SystemConfig
import com.ragnaroft.process_benchmarks.util.extension.LoggerDelegate
import com.rappi.reporting.ingestor.entrypoint.router.base.MainRouter
import io.reactivex.Completable
import io.vertx.core.Future
import io.vertx.reactivex.CompletableHelper
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router

class MainVerticle @Inject constructor(
    private val mainRouter: MainRouter,
    private val systemConfig: SystemConfig
) : AbstractVerticle() {

    private val logger by LoggerDelegate()

    override fun start(startFuture: Future<Void>) {
        startServer().subscribe(CompletableHelper.toObserver(startFuture))
    }

    private fun startServer(): Completable {
        val router = Router.router(vertx).mountSubRouter(systemConfig.basePath, mainRouter.create())

        val server = vertx.createHttpServer()
        server.requestStream()
            .toFlowable()
            .onBackpressureBuffer(512)
            .onBackpressureDrop { it.response().setStatusCode(503).end() }
            .subscribe(router::handle, ::logError)

        val port = config().getInteger("http.port", 8080)

        return server.rxListen(port)
            .doOnSuccess { logger.info("server is running at port $port...") }
            .ignoreElement()
    }

    private fun logError(t: Throwable) {
        logger.error("Error handling request: ", t)
    }
}
