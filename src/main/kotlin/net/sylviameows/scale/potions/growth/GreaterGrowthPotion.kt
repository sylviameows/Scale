package net.sylviameows.scale.potions.growth

import io.papermc.paper.potion.PotionMix
import net.sylviameows.scale.Core
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.RecipeChoice

class GreaterGrowthPotion(plugin: Core) : GrowthPotion(plugin) {
    override val identifier: NamespacedKey = NamespacedKey("scale", "strong_growth_potion")
    override val duration: Long
        get() = (super.duration * 0.75).toLong()

    override val effectName: String
        get() = super.effectName + "I"

    override val modifier: Double = 3.0

    override fun mix(): PotionMix {
        return PotionMix(
            identifier,
            item(),
            RecipeChoice.ExactChoice(GrowthPotion(super.plugin).item()),
            RecipeChoice.MaterialChoice(Material.DIAMOND)
        )
    }
}