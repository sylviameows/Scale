package net.sylviameows.scale.potions

import net.sylviameows.scale.Core
import net.sylviameows.scale.potions.tasks.PotionTask
import org.bukkit.entity.Player

class PotionManager(private val core: Core) {
    private val active = HashMap<String, PotionTask>()

    fun setActive(player: Player, task: PotionTask?) {
        if (task == null) {
            active.remove(player.name)
            return
        }

        val current = active[player.name];
        if (current != null && task.potion.effectName == current.potion.effectName) {
            current.remaining += task.remaining
            return;
        } else current?.end()

        task.runTaskTimer(core, 0L, 1L)
        active[player.name] = task;
    }

    fun setActive(task: PotionTask) {
        setActive(task.player, task);
    }

    fun clearEffect(player: Player) {
        active.remove(player.name)
    }

    fun getActive(player: Player): PotionTask? {
        return active[player.name];
    }

}