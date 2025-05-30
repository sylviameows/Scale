package net.sylviameows.scale.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.sylviameows.kitti.api.commands.Command
import net.sylviameows.kitti.api.commands.Context
import net.sylviameows.kitti.api.commands.Options
import net.sylviameows.kitti.api.commands.Result
import net.sylviameows.scale.Core
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

open class ScaleCommand : Command {
    override val name: String = "scale"

    open val min = 0.88
    open val max = 1.11

    override fun options(options: Options): Options {
        val value = DoubleArgumentType.doubleArg(min, max)
        val player = ArgumentTypes.player()

        return options
            .subcommand("reset") {
                it.argument("player", player) { arg ->
                    arg.permission("scale.others")
                }
            }
            .subcommand("get") {
                it.argument("player", player) { arg ->
                    arg.permission("scale.others")
                }
            }
            .subcommand("set") {
                it.argument("value", value)
                it.argument("player", player) { arg ->
                    arg.permission("scale.others")
                };
            }
            .argument("value", value)
            .argument("player", player) { arg ->
                arg.permission("scale.others")
            }
    }

    override fun execute(context: Context): Result {
        val sender = context.source.sender;

        val target: Player
        var other = false;
        if (context.has("player")) {
            val selector = context.getRequired<PlayerSelectorArgumentResolver>("player")
            val resolve = selector.resolve(context.source);

            target = resolve.first()
            other = target != sender;

            if (other && context.subcommand != "get" && !sender.hasPermission("scale.others")) {
                sender.sendMessage(Core.parse("<red>You do not have permission to set other player's scale!"))
                return Result.success();
            }
        } else {
            if (sender !is Player) {
                sender.sendMessage(Core.parse("<red>You are not a player!"))
                return Result.success();
            }

            target = sender;
        }

        val pdc = target.persistentDataContainer;
        if (Core.KEY == null) throw IllegalStateException("no data key defined!")

        if (context.subcommand == "reset") {
            pdc.set(Core.KEY, PersistentDataType.DOUBLE, 1.0)
            Core.updateScale(target, 1.0)

            return Result.success();
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

        return Result.success();
    }

    private fun ternary(boolean: Boolean, t: String, f: String): String {
        return if (boolean) t
        else f
    }

}