package net.sylviameows.scale

import io.papermc.paper.potion.PotionMix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.sylviameows.scale.potions.growth.GrowthPotion
import net.sylviameows.scale.potions.Potion
import net.sylviameows.scale.potions.PotionManager
import net.sylviameows.scale.potions.growth.GreaterGrowthPotion
import net.sylviameows.scale.potions.shrinking.GreaterShrinkingPotion
import net.sylviameows.scale.potions.shrinking.ShrinkingPotion
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import org.bukkit.inventory.RecipeChoice
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Core : JavaPlugin(), Listener {
    val potionManager = PotionManager(this)
    private val potions = ArrayList<Potion>()

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

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Logger.set(this.componentLogger)

        registerPotion(GrowthPotion(this))
        registerPotion(GreaterGrowthPotion(this))

        registerPotion(ShrinkingPotion(this))
        registerPotion(GreaterShrinkingPotion(this))
    }

    private fun registerPotion(potion: Potion) {
        potions.add(potion);

        val splashKey = NamespacedKey(potion.identifier.namespace, potion.identifier.key+"_splash")
        server.potionBrewer.addPotionMix(potion.mix())
        server.potionBrewer.addPotionMix(PotionMix(
            splashKey,
            potion.item().withType(Material.SPLASH_POTION),
            RecipeChoice.ExactChoice(potion.item()),
            RecipeChoice.MaterialChoice(Material.GUNPOWDER)
        ))
    }

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val player = event.player;
        val pdc = player.persistentDataContainer

        potions.forEach { potion ->
            potion.getAttributes(player)?.forEach { it.instance.removeModifier(potion.identifier) }
        }

        potions.forEach {
            val remaining = pdc.get(it.identifier, PersistentDataType.LONG)
            if (remaining != null) {
                it.apply(player,remaining);
            }
        }

        if (KEY == null) throw IllegalStateException("no data key defined!")
        if (pdc.has(KEY, PersistentDataType.DOUBLE)) {
            updateScale(player, pdc.getOrDefault(KEY, PersistentDataType.DOUBLE, 1.0))
        }
    }

    @EventHandler
    fun leave(event: PlayerQuitEvent) {
        val player = event.player;

        potionManager.setActive(player, null);
    }

    @EventHandler
    fun entityLoad(event: EntitiesLoadEvent) {
        event.entities.forEach {
            val data = it.persistentDataContainer;

            potions.forEach { effect ->
                val remaining = data.get(effect.identifier, PersistentDataType.LONG)
                if (remaining != null) {
                    effect.applyEntity(it, remaining)
                }
            }
        }
    }

    @EventHandler
    fun entityUnload(event: EntitiesUnloadEvent) {
        event.entities.forEach {
            potionManager.getActive(it)?.end(clear=true, persist=true)
            potionManager.clearEffect(it)
        }
    }


    @EventHandler
    fun land(event: PotionSplashEvent) {
        if (event.potion.effects.isNotEmpty()) return;
        val data = event.potion.item.persistentDataContainer

        val potion = potions.filter { data.has(it.identifier) }
        if (potion.isEmpty()) return;

        val effect = potion.first();
        for (entity in event.affectedEntities) {
            effect.applyEntity(entity, (event.getIntensity(entity) * effect.duration).toLong())
        }
    }

    @EventHandler
    fun consume(event: PlayerItemConsumeEvent) {
        if (event.item.type == Material.MILK_BUCKET) {
            potionManager.getActive(event.player)?.end(true)
            return
        }
        if (event.item.type != Material.POTION) return;

        val pdc = event.item.persistentDataContainer;
        potions.forEach {
            if (pdc.has(it.identifier)) it.apply(event.player);
        }
    }


}