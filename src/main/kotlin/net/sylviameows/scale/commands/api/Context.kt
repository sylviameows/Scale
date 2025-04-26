package net.sylviameows.scale.commands.api

import io.papermc.paper.command.brigadier.CommandSourceStack

class Context private constructor(val arguments: Map<String, Any?>, val source: CommandSourceStack, val subcommand: String?) {
    fun has(key: String): Boolean {
        return arguments.containsKey(key);
    }

    fun count(): Int {
        return arguments.count()
    }

    inline fun <reified T : Any> get(key: String): T? {
        val value = arguments[key]
        if (value !is T?) throw IllegalArgumentException("argument does not match type.")
        return value;
    }

    inline fun <reified T : Any> getRequired(key: String): T {
        val value = get<T>(key) ?: throw IllegalArgumentException("required argument is null.")
        return value;
    }

    class Builder {
        private val arguments: MutableMap<String, Any> = HashMap()
        private var subcommand: String? = null;

        fun set(key: String, value: Any): Builder {
            arguments[key] = value;
            return this;
        }

        fun subcommand(subcommand: String): Builder {
            this.subcommand = subcommand
            return this;
        }

        fun build(source: CommandSourceStack): Context {
            return Context(arguments.toMap(), source, subcommand);
        }

    }
}