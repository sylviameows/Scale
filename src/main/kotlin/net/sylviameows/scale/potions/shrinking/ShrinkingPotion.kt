package net.sylviameows.scale.potions.shrinking

import io.papermc.paper.potion.PotionMix
import net.sylviameows.scale.Core
import net.sylviameows.scale.potions.Potion
import net.sylviameows.scale.potions.PotionManager
import net.sylviameows.scale.potions.tasks.PotionTask
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.attribute.AttributeModifier.Operation
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionType

open class ShrinkingPotion(override val plugin: Core) : Potion {
    override val name: String = "Shrinking"
    override val effectName: String = "Shrink I"
    override val duration: Long = 4 * 60 * 20
    override val identifier: NamespacedKey = NamespacedKey("scale", "lesser_shrinking")

    override val modifier: Double = -0.5

    override fun manager(): PotionManager {
        return plugin.potionManager;
    }

    override fun item(): ItemStack {
        val item = super.item()
        item.editMeta { meta ->
            if (meta is PotionMeta) meta.color = Color.AQUA
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
            RecipeChoice.MaterialChoice(Material.BROWN_MUSHROOM)
        )
    }

    override fun getAttributes(player: Player): List<Potion.AttributeSettings>? {
        val scaleAttribute = player.getAttribute(Attribute.SCALE) ?: return null;
        val jumpHeightAttribute = player.getAttribute(Attribute.JUMP_STRENGTH) ?: return null;
        val stepHeightAttribute = player.getAttribute(Attribute.STEP_HEIGHT) ?: return null;
        return listOf(
            Potion.AttributeSettings.of(scaleAttribute),
            Potion.AttributeSettings.of(jumpHeightAttribute, 0.2),
            Potion.AttributeSettings.of(stepHeightAttribute)
        )
    }
}