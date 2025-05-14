package net.sylviameows.scale.potions.shrinking

import io.papermc.paper.potion.PotionMix
import net.sylviameows.scale.Core
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.RecipeChoice

class GreaterShrinkingPotion(plugin: Core) : ShrinkingPotion(plugin) {
    override val identifier: NamespacedKey = NamespacedKey("scale", "strong_shrinking_potion")
    override val duration: Long
        get() = (super.duration * 0.75).toLong()

    override val level = 2;

    override val modifier: Double = -0.75

    override fun mix(): PotionMix {
        return PotionMix(
            identifier,
            item(),
            RecipeChoice.ExactChoice(ShrinkingPotion(super.plugin).item()),
            RecipeChoice.MaterialChoice(Material.DIAMOND)
        )
    }
}