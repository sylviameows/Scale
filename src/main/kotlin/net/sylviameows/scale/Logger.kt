package net.sylviameows.scale

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

class Logger {
    companion object {
        private var logger: ComponentLogger? = null;
        val cache = ArrayList<Component>();

        fun get(): ComponentLogger {
            if (logger == null) throw IllegalStateException("logger not initialized.")
            return logger!!;
        }

        fun print(component: Component) {
            if (logger == null) {
                cache.add(component)
                return;
            }

            logger!!.info(component);
        }

        fun print(string: String) {
            print(Component.text(string))
        }

        fun set(logger: ComponentLogger) {
            if (cache.size > 0) {
                cache.forEach(logger::info)
            }
            Companion.logger = logger;
        }
    }
}