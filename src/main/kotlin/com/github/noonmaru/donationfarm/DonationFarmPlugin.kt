package com.github.noonmaru.donationfarm

import org.bukkit.plugin.java.JavaPlugin

class DonationFarmPlugin : JavaPlugin() {
    override fun onEnable() {
        val customScheduler = Scheduler()

        server.apply {
            pluginManager.registerEvents(EventListener(customScheduler), this@DonationFarmPlugin)
            scheduler.runTaskTimer(this@DonationFarmPlugin, customScheduler, 0L, 1L)
        }
    }
}