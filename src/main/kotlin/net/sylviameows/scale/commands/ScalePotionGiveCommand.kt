package net.sylviameows.scale.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import net.sylviameows.kitti.api.commands.Command
import net.sylviameows.kitti.api.commands.Context
import net.sylviameows.kitti.api.commands.Options
import net.sylviameows.kitti.api.commands.Result
import net.sylviameows.scale.Core
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.InventoryHolder

class ScalePotionGiveCommand : Command {
    override val name: String = "spgive"

    override fun options(options: Options): Options {
        val potionType = StringArgumentType.word()

        options.argument("potion", potionType) { arg ->
            arg.suggestions = SuggestionProvider { ctx, builder ->
                Core.potions().forEach { potion ->
                    val name = potion.name.lowercase()
                    if (name.startsWith(builder.remaining, true)) {
                        builder.suggest(name)
                    }
                }
                return@SuggestionProvider builder.buildFuture()
            }
            return@argument arg;
        }
        options.argument("level", IntegerArgumentType.integer(1, 2))
        options.argument("splash", BoolArgumentType.bool())

        return options.permission("scale.give");
    }

    override fun execute(context: Context): Result {
        val name = context.get<String>("potion");
        val level = context.get<Int>("level")

        if (name == null) {
            return Result.fail("required argument 'potion' was not provided.")
        }
        if (level == null) {
            return Result.fail("required argument 'level' was not provided.")
        }

        val sameTypePotions = Core.potions().filter { it.name.lowercase() == name.lowercase() };
        if (sameTypePotions.isEmpty()) {
            return Result.fail("potion name '$name' is not a registered potion type!")
        }

        val targetPotion = sameTypePotions.filter { it.level == level }
        if (targetPotion.isEmpty()) {
            return Result.fail("potion name '$name' does not have a tier $level effect")
        }
        val potion = targetPotion.first()

        val splash = context.get<Boolean>("splash") ?: false

        val sender: CommandSender = context.source.sender;
        if (sender !is InventoryHolder) {
            return Result.fail("sender must have an inventory to give to!")
        }

        var item = potion.item()
        if (splash) {
            item = item.withType(Material.SPLASH_POTION);
        }
        sender.inventory.addItem(item);
        sender.sendMessage(Core.parse("<white>Gave you a <aqua>${potion.effectName}</aqua> potion!"))

        return Result.success()
    }
}