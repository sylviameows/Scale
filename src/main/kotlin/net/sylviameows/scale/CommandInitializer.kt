package net.sylviameows.scale

import com.mojang.brigadier.arguments.DoubleArgumentType
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.sylviameows.scale.commands.OverrideScaleCommand
import net.sylviameows.scale.commands.ScaleCommand
import net.sylviameows.scale.commands.api.Command

class CommandInitializer(private val registrar: Commands) {
    init {
        add(ScaleCommand())
        add(OverrideScaleCommand())

        registrar.register(Commands.literal("test")
            .then(Commands.argument("test1", DoubleArgumentType.doubleArg()))
            .then(Commands.argument("test2", ArgumentTypes.player()))
            .build())
    }

    private fun add(command: Command) {
        registrar.register(command.options().build())
    }
}