package net.sylviameows.scale.commands.api

import com.mojang.brigadier.arguments.ArgumentType

data class Argument<T : Any>(val argumentType: ArgumentType<T>, val resultType: Class<T>) {
    companion object {
        inline fun <reified T : Any> of(type: ArgumentType<T>): Argument<T> {
            return Argument(type, T::class.java)
        }
    }
}