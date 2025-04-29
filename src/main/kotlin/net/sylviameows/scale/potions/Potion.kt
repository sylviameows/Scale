package net.sylviameows.scale.potions

import io.papermc.paper.potion.PotionMix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.sylviameows.scale.Core
import net.sylviameows.scale.potions.tasks.PotionTask
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

interface Potion {
    val name: String
    val effectName: String;
    val duration: Long;
    val modifier: Double;

    val plugin: Core

    val identifier: NamespacedKey

    fun mix(): PotionMix

    fun manager(): PotionManager

    fun item(): ItemStack {
        val item = ItemStack(Material.POTION);
        item.editPersistentDataContainer {
            it.set(identifier, PersistentDataType.BOOLEAN, true)
        }

        item.editMeta {
            it.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            it.displayName(Component.text("Potion of $name").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
            it.lore(listOf(Component.text("$effectName (${parseDuration(duration)})")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(NamedTextColor.BLUE)
            ))
        }

        return item;
    }

    fun parseDuration(duration: Long): String {
        var seconds = duration.toInt() / 20;
        val minutes = seconds / 60;
        if (minutes > 0) seconds -= (minutes * 60)

        var secondsString: String = seconds.toString();
        if (seconds < 10) secondsString = "0$secondsString"

        if (minutes >= 10) return "${minutes}:${secondsString}"
        return "0${minutes}:${secondsString}";
    }

    fun apply(player: Player, duration: Long = this.duration): PotionTask? {
        return applyEntity(player, duration)
    }

    fun applyEntity(entity: Entity, duration: Long = this.duration): PotionTask? {
        if (entity !is Attributable) return null;
        val attributes = getAttributes(entity) ?: return null;

        if (plugin.potionManager.getActive(entity)?.potion?.effectName != effectName) {
            attributes.forEach {
                it.instance.addModifier(
                    AttributeModifier(identifier,
                        modifier * it.modifier,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
            }
        }

        val task = PotionTask(entity, attributes, this, duration / 2);
        plugin.potionManager.setActive(task);

        return task;
    }

    fun getAttributes(attributable: Attributable): List<AttributeSettings>?;
    
    data class AttributeSettings(val instance: AttributeInstance, val modifier: Double) {
        companion object {
            fun of(instance: AttributeInstance?, modifier: Double = 1.0): AttributeSettings? {
                if (instance == null) return null;
                return AttributeSettings(instance, modifier)
            }
        }
    }
}