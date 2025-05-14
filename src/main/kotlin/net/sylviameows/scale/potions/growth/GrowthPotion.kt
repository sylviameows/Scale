package net.sylviameows.scale.potions.growth

import io.papermc.paper.potion.PotionMix
import net.sylviameows.scale.Core
import net.sylviameows.scale.potions.Potion
import net.sylviameows.scale.potions.PotionManager
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionType

open class GrowthPotion(override val plugin: Core) : Potion {
    override val name: String = "Growing"
    override val level: Int = 1;
    override val effectName: String
        get() {
            var levelString = ""
            repeat(level) { levelString += "I" }
            return "Growth $levelString"
        }
    override val duration: Long = 4 * 60 * 20
    override val identifier: NamespacedKey = NamespacedKey("scale", "lesser_growth")

    override val modifier: Double = 1.0

    override fun manager(): PotionManager {
        return plugin.potionManager;
    }

    override fun item(): ItemStack {
        val item = super.item()
        item.editMeta { meta ->
            if (meta is PotionMeta) meta.color = Color.LIME
        }
        return item
    }

    override fun mix(): PotionMix {
        val thickPotion = ItemStack(Material.POTION)
        thickPotion.editMeta { meta ->
            if (meta is PotionMeta) meta.basePotionType = PotionType.THICK
        }

        return PotionMix(
            identifier,
            item(),
            RecipeChoice.ExactChoice(thickPotion),
            RecipeChoice.MaterialChoice(Material.RED_MUSHROOM)
        )
    }

    override fun getAttributes(attributable: Attributable): List<Potion.AttributeSettings>? {
        val list = ArrayList<Potion.AttributeSettings?>();

        list.add(Potion.AttributeSettings.of(attributable.getAttribute(Attribute.SCALE)))
        list.add(Potion.AttributeSettings.of(attributable.getAttribute(Attribute.BLOCK_INTERACTION_RANGE), 0.5))
        list.add(Potion.AttributeSettings.of(attributable.getAttribute(Attribute.ENTITY_INTERACTION_RANGE), 0.5))
        list.add(Potion.AttributeSettings.of(attributable.getAttribute(Attribute.JUMP_STRENGTH), 0.5))
        list.add(Potion.AttributeSettings.of(attributable.getAttribute(Attribute.STEP_HEIGHT)))
        list.add(Potion.AttributeSettings.of(attributable.getAttribute(Attribute.SAFE_FALL_DISTANCE)))

        return list.filterNotNull()
    }
}