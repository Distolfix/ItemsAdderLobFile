package net.fantasydreams.itemsAdderLobFile;

import net.fantasydreams.itemsAdderLobFile.api.LobFileAPI;
import net.fantasydreams.itemsAdderLobFile.commands.CustomLobFileCommand;
import net.fantasydreams.itemsAdderLobFile.config.ItemsAdderConfigUpdater;
import net.fantasydreams.itemsAdderLobFile.scheduler.PackUpdateTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;

import java.io.File;

public final class ItemsAdderLobFile extends JavaPlugin {

    private LobFileAPI lobFileAPI;
    private ItemsAdderConfigUpdater configUpdater;
    private BukkitTask updateTask;
    private FileConfiguration secretConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSecretConfig();

        configUpdater = new ItemsAdderConfigUpdater();
        loadConfiguration();

        if (!configUpdater.isItemsAdderPresent()) {
            getLogger().warning("ItemsAdder not found! The plugin will still work but won't be able to update configurations until ItemsAdder is installed.");
        }

        // Register the command manually for Paper plugins
        try {
            CustomLobFileCommand lobfileCommand = new CustomLobFileCommand(this, lobFileAPI, configUpdater);
            getServer().getCommandMap().register(getDescription().getName(), lobfileCommand);
            getLogger().info("Successfully registered lobfile command");
        } catch (Exception e) {
            getLogger().severe("Failed to register lobfile command: " + e.getMessage());
        }

        startUpdateTask();

        getLogger().info("ItemsAdderLobFile plugin enabled successfully!");
    }

    public FileConfiguration getSecretConfig() {
        return secretConfig;
    }

    @Override
    public void onDisable() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
        }
        getLogger().info("ItemsAdderLobFile plugin disabled successfully!");
    }

    public void loadConfiguration() {
        String apiKey = secretConfig.getString("lobfile.api-key", "yD8FqkvG2ch4pKzL");
        String baseUrl = secretConfig.getString("lobfile.base-url", "https://lobfile.com/api/v3");

        // Debug logging removed to reduce console spam

        lobFileAPI = new LobFileAPI(apiKey, baseUrl, getLogger());

        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
        }
        startUpdateTask();
    }

    private void loadSecretConfig() {
        File secretFile = new File(getDataFolder(), "secret.yml");
        if (!secretFile.exists()) {
            saveResource("secret.yml", false);
        }
        secretConfig = YamlConfiguration.loadConfiguration(secretFile);

        // Auto-fix old API version
        String currentBaseUrl = secretConfig.getString("lobfile.base-url", "");
        if (currentBaseUrl.contains("/api/v1")) {
            getLogger().info("Detected old API version (v1), automatically updating to v3...");
            secretConfig.set("lobfile.base-url", "https://lobfile.com/api/v3");
            try {
                secretConfig.save(secretFile);
                getLogger().info("Successfully updated secret.yml to use API v3");
            } catch (Exception e) {
                getLogger().severe("Failed to update secret.yml: " + e.getMessage());
            }
        }
    }

    private void startUpdateTask() {
        int intervalMinutes = secretConfig.getInt("lobfile.check-interval-minutes", 1);
        long intervalTicks = intervalMinutes * 60L * 20L;

        PackUpdateTask task = new PackUpdateTask(this, lobFileAPI, configUpdater);
        updateTask = task.runTaskTimer(this, 20L, intervalTicks);

        getLogger().info("Started automatic pack update checks every " + intervalMinutes + " minute(s)");
    }
}
