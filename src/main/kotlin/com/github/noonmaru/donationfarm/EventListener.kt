package com.github.noonmaru.donationfarm

import com.github.noonmaru.twipe.event.AsyncTwipDonateEvent
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.world.ChunkLoadEvent

class EventListener(
    private val scheduler: Scheduler
) : Listener {
    @EventHandler
    fun onDonate(event: AsyncTwipDonateEvent) {
        val donator = event.nickname
        val amount = event.amount
        val playerName = event.streamer
        val message = event.comment

        when {
            amount >= 100_000 -> {
                scheduler.addTask(SpawnTask(donator, amount, playerName, message, listOf(Mob.DRAGON to 1)))
            }
            amount >= 20000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.HORSE to 10,
                            Mob.SKELETON_HORSE to 1,
                        )
                    )
                )
            }
            amount >= 10000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.PANDA to 10,
                            Mob.POLAR_BEAR to 1
                        )
                    )
                )
            }
            amount >= 5000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.COW to 20,
                            Mob.MUSHROOM_COW to 1,
                            Mob.RAVAGER to 1
                        )
                    )
                )
            }
            amount >= 4000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.PIG to 1
                        )
                    )
                )
            }
            amount >= 3000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.SHEEP to 20,
                            Mob.WOLF to 1,
                        )
                    )
                )
            }
            amount >= 2000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.RABBIT to 20,
                            Mob.FOX to 1,
                        )
                    )
                )
            }
            amount >= 1000 -> {
                scheduler.addTask(
                    SpawnTask(
                        donator, amount, playerName, message, listOf(
                            Mob.SALMON to 5,
                            Mob.PUFFERFISH to 1,
                        )
                    )
                )
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity is Ravager) return

        if (entity.customName != null) {
            if (event is EntityDamageByEntityEvent) {
                if (event.damager is Player) {
                    event.isCancelled = true
                }
            } else {
                val cause = event.cause

                if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.LAVA || cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                    event.isCancelled = true
                    entity.fireTicks = 0
                }
            }
        }
    }

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        if (event.isNewChunk) {
            val chunk = event.chunk
            for (entity in chunk.entities) {
                if (entity !is Player && entity !is Villager && entity !is Monster) {
                    entity.remove()
                }
            }
        }
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity

        if (event.spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL) {
            if (entity !is Monster) {
                event.isCancelled = true
            }
        }
    }
}