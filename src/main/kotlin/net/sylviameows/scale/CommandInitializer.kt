package net.sylviameows.scale

import io.papermc.paper.command.brigadier.Commands
import net.sylviameows.scale.commands.OverrideScaleCommand
import net.sylviameows.scale.commands.ScaleCommand
import net.sylviameows.scale.commands.api.Command

class CommandInitializer(private val registrar: Commands) {
    init {
        add(ScaleCommand())
        add(OverrideScaleCommand())
    }

    private fun add(command: Command) {
        registrar.register(command.options().build())
    }
}