package com.ragnaroft.process_benchmarks.util.extension

import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.LoggerHandler

fun Router.post(path: String, rctx: RoutingContext.() -> Unit) = post(path)
    .handler { it.rctx() }

fun Router.put(path: String, rctx: RoutingContext.() -> Unit) = put(path).handler { it.rctx() }
    .handler(LoggerHandler.create())

fun Router.health(path: String, rctx: RoutingContext.() -> Unit) = get(path)
    .handler { it.rctx() }

fun Router.delete(path: String, rctx: RoutingContext.() -> Unit) = delete(path)
    .handler { it.rctx() }

fun Router.get(path: String, rctx: RoutingContext.() -> Unit) = get(path)
    .handler { it.rctx() }
