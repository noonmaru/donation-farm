package com.github.noonmaru.donationfarm

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.PolarBear
import java.util.concurrent.LinkedBlockingDeque

class Scheduler: Runnable {
    private val queue = LinkedBlockingDeque<Runnable>()

    fun addTask(runnable: Runnable) {
        queue.offer(runnable)
    }

    override fun run() {
        while (queue.isNotEmpty()) {
            queue.remove().run()
        }

        for (world in Bukkit.getWorlds()) {
            for (polarBear in world.getEntitiesByClass(PolarBear::class.java)) {
                for (nearbyEntity in polarBear.getNearbyEntities(8.0, 4.0, 8.0)) {
                    if (nearbyEntity is LivingEntity && nearbyEntity !is Player && nearbyEntity !is PolarBear) {
                        if (polarBear != null)
                            polarBear.target = nearbyEntity
                    }
                }
            }
        }
    }
}