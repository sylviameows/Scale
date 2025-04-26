package net.sylviameows.scale.commands.api

interface Command {
    val name: String;

    fun options(): Options {
        return Options.of(this)
    }
    fun execute(context: Context): Int
}