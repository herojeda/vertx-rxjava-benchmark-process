package com.rappi.reporting.ingestor.entrypoint.router.base

import com.ragnaroft.process_benchmarks.entrypoint.router.base.BaseRouter
import com.ragnaroft.process_benchmarks.entrypoint.router.base.route.Routes
import com.ragnaroft.process_benchmarks.exception.handler.ErrorHandler
import com.ragnaroft.process_benchmarks.util.extension.get
import io.vertx.core.json.Json
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.handler.BodyHandler
import javax.inject.Inject

/**
 * Creates the main router for this application, new routes should be
 * added here.
 */
class MainRouter @Inject constructor(
    private val vertx: Vertx,
    private val subRouters: Set<@JvmSuppressWildcards BaseRouter>
) {

    fun create(): Router {

        val router = Router.router(vertx)

        router.apply {
            route().handler(BodyHandler.create())
            router.route("/*").failureHandler(ErrorHandler())

            subRouters.forEach { mountSubRouter(it.getPath(), it.create()) }
        }

        /*Health Check*/
        router.get(Routes.HEALTH) {
            this.response().end(Json.encode(mapOf("status" to "OK")))
        }

        return router
    }
}
