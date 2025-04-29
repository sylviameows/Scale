package net.sylviameows.scale.potions.tasks

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.sylviameows.scale.potions.Potion
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable

class PotionTask(val entity: Entity, private val attributes: List<Potion.AttributeSettings>, val potion: Potion, val duration: Long) : BukkitRunnable() {
    var remaining: Long = duration;

    init {
        potion.manager().setActive(entity, this);
    }

    override fun run() {
        remaining--
        if (remaining % 20 == 0L) {
            val component = Component.text("${potion.effectName} (${potion.parseDuration(remaining)})")
                .color(NamedTextColor.BLUE)

            entity.sendActionBar(component)
        }

        entity.persistentDataContainer.set(potion.identifier, PersistentDataType.LONG, remaining)
        if (remaining > 0) return;

        end(true)
    }

    fun end(clear: Boolean = true, persist: Boolean = false) {
        if (clear) attributes.forEach { it.instance.removeModifier(potion.identifier) }
        if (!persist) entity.persistentDataContainer.remove(potion.identifier)
        potion.manager().clearEffect(entity)
        this.cancel()
    }
}