package com.github.noonmaru.donationfarm

import com.google.common.collect.ImmutableList
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.entity.*
import org.bukkit.material.Colorable
import kotlin.random.Random.Default.nextInt

class SpawnTask(
    private val donator: String,
    private val amount: Int,
    private val playerName: String,
    private val message: String,
    mobs: List<Pair<Mob, Int>>
) : Runnable {
    private val mobs: List<Pair<Mob, Int>>
    private val max: Int

    init {
        val list = arrayListOf<Pair<Mob, Int>>()
        var prev = 0

        for (mob in mobs) {
            list += mob.first to mob.second + prev
            prev += mob.second
        }

        this.mobs = ImmutableList.copyOf(list)
        this.max = prev
    }

    override fun run() {
        val location = Bukkit.getPlayer(playerName)?.location ?: Bukkit.getWorlds().first().spawnLocation
        val world = location.world

        val rand = nextInt(max)

        for ((mob, value) in mobs) {
            if (rand < value) {
                world.spawn(location, mob.entityClass) { entity ->
                    val color = if (playerName == "Heptagram") ChatColor.YELLOW else ChatColor.DARK_GREEN
                    entity.customName = "${color}${donator} ${mob.name}"
                    entity.isCustomNameVisible = true

                    if (entity is Colorable) {
                        entity.color = DyeColor.values().random()
                    }
                    if (entity is Ageable) {
                        if (mob.adult) {
                            entity.setAdult()
                        } else {
                            entity.setBaby()
                        }
                    }

                    mob.post?.invoke(entity)
                    Bukkit.broadcastMessage("${ChatColor.GOLD}${donator}${ChatColor.RESET}님이 ${playerName}에게 ${color}${mob.name}${ChatColor.RESET}(을)를 소환했습니다. [$amount] $message")
                }
                break
            }
        }
    }
}

class Mob(
    val name: String,
    val entityClass: Class<out Entity>,
    val adult: Boolean,
    val post: ((Entity) -> Unit)? = null
) {
    companion object {
        fun register(
            name: String,
            entityClass: Class<out Entity>,
            adult: Boolean,
            post: ((Entity) -> Unit)? = null
        ): Mob {
            return Mob(name, entityClass, adult, post)
        }

        val SALMON = register("연어", Salmon::class.java, true)
        val PUFFERFISH = register("복어", PufferFish::class.java, true)
        val RABBIT = register("토끼", Rabbit::class.java, true)
        val FOX = register("여우", Fox::class.java, true)
        val SHEEP = register("양", Sheep::class.java, false)
        val WOLF = register("늑대", Wolf::class.java, true)
        val PIG = register("돼지", Pig::class.java, false)
        val COW = register("소", Cow::class.java, false)
        val MUSHROOM_COW = register("버섯소", MushroomCow::class.java, false)
        val RAVAGER = register("파괴수", Ravager::class.java, true)
        val PANDA = register("판다", Panda::class.java, false)
        val POLAR_BEAR = register("북극곰", PolarBear::class.java, true)
        val HORSE = register("말", Horse::class.java, false)
        val SKELETON_HORSE = register("스켈레톤 말", SkeletonHorse::class.java, false) {entity ->
            for (nearEntity in entity.getNearbyEntities(8.0, 4.0, 8.0)) {
                if (nearEntity is LivingEntity && nearEntity !is Player && nearEntity !is Animals) {
                    val loc = nearEntity.location
                    val name = nearEntity.customName
                    nearEntity.remove()
                    loc.world.spawn(loc, Zombie::class.java)
                    nearEntity.customName = name
                    nearEntity.isCustomNameVisible = true
                }
            }
        }
        val LLAMA = register("라마", Llama::class.java, false)
        val CAT = register("고양이", Cat::class.java, false)
        val DRAGON = register("드래곤", EnderDragon::class.java, false)
    }
}