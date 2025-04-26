package net.sylviameows.scale.commands.api

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.permissions.Permission

class Options private constructor(private val name: String, private val callback: (Context) -> Int, private val parent: Options? = null) {
    private val arguments: MutableMap<String, Argument<out Any>> = HashMap()
    private var permission: String? = null;
    private val subcommands: MutableList<Options> = ArrayList()

    companion object {
        fun of(command: Command): Options {
            return Options(command.name, command::execute)
        }
    }

    fun permission(permission: String): Options {
        this.permission = permission;
        return this;
    }

    fun argument(name: String, type: Argument<out Any>): Options {
        arguments[name] = type
        return this
    }

    inline fun <reified T : Any> argument(name: String, type: ArgumentType<T>): Options {
        return argument(name, Argument.of(type))
    }

    fun subcommand(name: String, options: (Options) -> Options = { it }): Options {
        subcommands.add(options(Options(name, callback, this)))
        return this
    }

    private fun getContext(ctx: CommandContext<CommandSourceStack>, subcommand: String? = null): Context {
        val context = Context.Builder()

        if (!subcommand.isNullOrEmpty()) context.subcommand(subcommand)
        for (pair in arguments) {
            val name = pair.key
            val argument = pair.value
            try {
                val value = ctx.getArgument(name, argument.resultType);
                if (value != null) context.set(name, value)
            } catch (_: IllegalArgumentException) {

            }
        }

        return context.build(ctx.source);
    }

    private fun subcommand(): String? {
        if (parent != null) {
            var subcommand = name
            var parent = this.parent

            while (parent != null) {
                if (parent.parent == null) break
                subcommand = parent.name + " " + subcommand
                parent = parent.parent
            }

            return subcommand
        }

        return null
    }

    private fun convert(): LiteralArgumentBuilder<CommandSourceStack> {
        val literal = Commands.literal(name)
        val subcommand = subcommand()

        literal.executes { callback(getContext(it, subcommand)) }

        var parent: ArgumentBuilder<CommandSourceStack,*> = literal;
        for (pair in arguments) {
            val name = pair.key
            val data = pair.value;

            val argument = Commands.argument(name, data.argumentType)
            parent.then(argument.executes { callback(getContext(it, subcommand)) })

            parent = argument;
        }

        for (sub in subcommands) {
            literal.then(sub.convert())
        }

        literal.requires {
            if (permission != null) return@requires it.sender.hasPermission(permission!!);
            else return@requires true
        }

        return literal;
    }

    fun build(): LiteralCommandNode<CommandSourceStack> {
        return convert().build();
    }
}