package net.sylviameows.scale.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.sylviameows.scale.Core
import net.sylviameows.scale.commands.api.Command
import net.sylviameows.scale.commands.api.Context
import net.sylviameows.scale.commands.api.Options
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

open class ScaleCommand : Command {
    override val name: String = "scale"

    open val min = 0.88
    open val max = 1.11

    override fun options(): Options {
        val player = ArgumentTypes.player()
        val value = DoubleArgumentType.doubleArg(min, max)

        return super.options()
            .subcommand("reset") {
                it.argument("player", player)
            }
            .subcommand("get") {
                it.argument("player", player)
            }
            .subcommand("set") {
                it.argument("value", value)
                it.argument("player", player)
            }
            .argument("value", value)
            .argument("player", player)
    }

    override fun execute(context: Context): Int {
        val sender = context.source.sender;

        sender.sendMessage(context.subcommand ?: "none")

        val target: Player
        var other = false;
        if (context.has("player")) {
            val selector = context.getRequired<PlayerSelectorArgumentResolver>("player")
            val resolve = selector.resolve(context.source);

            target = resolve.first()
            other = target != sender;

            if (other && context.subcommand != "get" && !sender.hasPermission("scale.others")) {
                sender.sendMessage(Core.parse("<red>You do not have permission to set other player's scale!"))
                return 1;
            }
        } else {
            if (sender !is Player) {
                sender.sendMessage(Core.parse("<red>You are not a player!"))
                return 1;
            }

            target = sender;
        }

        val pdc = target.persistentDataContainer;
        if (Core.KEY == null) throw IllegalStateException("no data key defined!")

        if (context.subcommand == "reset") {
            pdc.set(Core.KEY, PersistentDataType.DOUBLE, 1.0)
            Core.updateScale(target, 1.0)

            return 1;
        }

        if (context.has("value")) {
            val value = context.getRequired<Double>("value")
            if (other) sender.sendMessage(Core.parse("<white>Set ${target.name}'s scale to <aqua><u>$value</u></aqua>!"))

            Core.updateScale(target, value)
            pdc.set(Core.KEY, PersistentDataType.DOUBLE, value)
        } else {
            val scale = pdc.getOrDefault(Core.KEY, PersistentDataType.DOUBLE, 1.0)
            sender.sendMessage(Core.parse("<white>${ternary(other, "${target.name}'s", "Your")} scale is currently <aqua><u>$scale</u></aqua>."))
        }

        return 1;
    }

    private fun ternary(boolean: Boolean, t: String, f: String): String {
        return if (boolean) t
        else f
    }

}