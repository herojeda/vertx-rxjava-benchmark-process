package com.ragnaroft.process_benchmarks.configuration

import com.google.inject.Guice
import com.ragnaroft.process_benchmarks.configuration.json.JsonMapperConfig
import com.ragnaroft.process_benchmarks.configuration.module.MainModule
import com.ragnaroft.process_benchmarks.configuration.parser.ConfigParser
import com.ragnaroft.process_benchmarks.configuration.verticle.MainVerticle
import com.ragnaroft.process_benchmarks.util.extension.LoggerDelegate
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.reactivex.core.Vertx

fun main(args: Array<String>) {

  // Route all vert.x logging to SLF4J
  System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory::class.java.name)

  val logger by LoggerDelegate()
  val vertx = Vertx.vertx()

  initVertx()

  val config = ConfigParser(vertx).read().blockingGet()
  val injector = Guice.createInjector(MainModule(vertx, config))

  val main = injector.getInstance(MainVerticle::class.java)

  vertx.deployVerticle(main) { ar ->
    if (ar.succeeded()) {
      logger.info("Application started")
    } else {
      logger.info("Could not start application")
      ar.cause().printStackTrace()
    }
  }

  Runtime.getRuntime().addShutdownHook(Thread {
    logger.info("Closing application")
    vertx.close()
  })
}

private fun initVertx() {
  JsonMapperConfig.applyConfig()
}
