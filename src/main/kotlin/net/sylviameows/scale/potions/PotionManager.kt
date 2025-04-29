package net.sylviameows.scale.potions

import net.sylviameows.scale.Core
import net.sylviameows.scale.potions.tasks.PotionTask
import org.bukkit.entity.Entity

class PotionManager(private val core: Core) {
    private val active = HashMap<String, PotionTask>()

    fun setActive(entity: Entity, task: PotionTask?) {
        val uuid = entity.uniqueId.toString()
        if (task == null) {
            active.remove(uuid)
            return
        }

        val current = active[uuid];
        if (current != null && task.potion.effectName == current.potion.effectName) {
            current.remaining += task.remaining
            return;
        } else current?.end()

        task.runTaskTimer(core, 0L, 1L)
        active[uuid] = task;
    }

    fun setActive(task: PotionTask) {
        setActive(task.entity, task);
    }

    fun clearEffect(entity: Entity) {
        active.remove(entity.uniqueId.toString())
    }


    fun getActive(entity: Entity): PotionTask? {
        return active[entity.uniqueId.toString()];
    }

}