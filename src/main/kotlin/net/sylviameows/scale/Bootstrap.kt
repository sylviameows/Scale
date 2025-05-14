package net.sylviameows.scale

import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import net.sylviameows.kitti.api.KittiBootstrapper
import net.sylviameows.scale.commands.OverrideScaleCommand
import net.sylviameows.scale.commands.ScaleCommand
import net.sylviameows.scale.commands.ScalePotionGiveCommand
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Bootstrap : KittiBootstrapper() {
    init {
        addCommands(ScaleCommand(), OverrideScaleCommand(), ScalePotionGiveCommand())
    }

    override fun createPlugin(context: PluginProviderContext): JavaPlugin {
        return Core()
    }
}