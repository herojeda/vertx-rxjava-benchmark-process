package com.ragnaroft.process_benchmarks.configuration.module

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import com.google.inject.multibindings.Multibinder
import com.ragnaroft.process_benchmarks.configuration.ConfigurationModel
import com.ragnaroft.process_benchmarks.configuration.RestClientConfig
import com.ragnaroft.process_benchmarks.configuration.SystemConfig
import com.ragnaroft.process_benchmarks.configuration.verticle.MainVerticle
import com.ragnaroft.process_benchmarks.entrypoint.router.base.BaseRouter
import com.ragnaroft.process_benchmarks.util.RestClient
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient

open class MainModule(private val vertx: Vertx, private val config: JsonObject) : AbstractModule() {

    override fun configure() {

        // Configs
        val configModel = config.mapTo(ConfigurationModel::class.java)
        bind(RestClientConfig::class.java).toInstance(configModel.restClient)
        bind(SystemConfig::class.java).toInstance(configModel.system)

        // Main
        bind(Vertx::class.java).toInstance(vertx)
        bind(WebClient::class.java).toInstance(WebClient.create(vertx))
        bind(MainVerticle::class.java).`in`(Singleton::class.java)
        bind(RestClient::class.java)

        //Routers
        val routerBinder = Multibinder.newSetBinder(binder(), BaseRouter::class.java)

    }

}
