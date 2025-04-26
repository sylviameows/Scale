package net.sylviameows.scale

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Core : JavaPlugin(), Listener {
    companion object {
        val KEY = NamespacedKey.fromString("scale:value")

        fun updateScale(player: Player, scale: Double) {
            player.sendMessage(parse("<white>Your scale has been set to <aqua><u>$scale</u></aqua>!"))

            val attribute: AttributeInstance = player.getAttribute(Attribute.SCALE) ?: return;
            attribute.baseValue = scale;
        }

        fun parse(text: String): Component {
            return MiniMessage.miniMessage().deserialize(text)
        }
    }

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val player = event.player;
        val pdc = player.persistentDataContainer

        if (KEY == null) throw IllegalStateException("no data key defined!")
        if (pdc.has(KEY, PersistentDataType.DOUBLE)) {
            updateScale(player, pdc.getOrDefault(KEY, PersistentDataType.DOUBLE, 1.0))
        }
    }


}