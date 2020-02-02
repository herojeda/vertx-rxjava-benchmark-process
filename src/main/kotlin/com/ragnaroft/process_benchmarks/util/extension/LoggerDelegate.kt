package com.ragnaroft.process_benchmarks.util.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

class LoggerDelegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {

        if (thisRef == null) {
            LoggerFactory.getLogger("DEFAULT").error("Trying to obtain a logger for a null instance {}", property)
            return LoggerFactory.getLogger("DEFAULT")
        }
        return LoggerFactory.getLogger(thisRef.javaClass)
    }
}
