package net.fantasydreams.itemsAdderLobFile.scheduler;

import net.fantasydreams.itemsAdderLobFile.ItemsAdderLobFile;
import net.fantasydreams.itemsAdderLobFile.api.LobFileAPI;
import net.fantasydreams.itemsAdderLobFile.config.ItemsAdderConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PackUpdateTask extends BukkitRunnable {
    private final ItemsAdderLobFile plugin;
    private final LobFileAPI lobFileAPI;
    private final ItemsAdderConfigUpdater configUpdater;

    public PackUpdateTask(ItemsAdderLobFile plugin, LobFileAPI lobFileAPI, ItemsAdderConfigUpdater configUpdater) {
        this.plugin = plugin;
        this.lobFileAPI = lobFileAPI;
        this.configUpdater = configUpdater;

        if (configUpdater == null) {
            plugin.getLogger().severe("PackUpdateTask initialized with null configUpdater!");
        }
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (configUpdater == null) {
                    plugin.getLogger().severe("configUpdater is null in PackUpdateTask.run()");
                    return;
                }

                if (!configUpdater.isItemsAdderPresent()) {
                    plugin.getLogger().warning("ItemsAdder config.yml not found. Skipping update.");
                    return;
                }

                String latestUrl = lobFileAPI.getLatestPackDownloadUrl();
                String currentUrl = configUpdater.getCurrentExternalHostUrl();

                if (latestUrl != null && !latestUrl.equals(currentUrl)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (configUpdater.updateExternalHostUrl(latestUrl)) {
                            plugin.getLogger().info("Updated ItemsAdder external host URL to: " + latestUrl);

                            if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iazip");
                                plugin.getLogger().info("Triggered ItemsAdder resource pack regeneration");
                            }
                        } else {
                            plugin.getLogger().severe("Failed to update ItemsAdder configuration");
                        }
                    });
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error checking for pack updates: " + e.getMessage());
            }
        });
    }
}