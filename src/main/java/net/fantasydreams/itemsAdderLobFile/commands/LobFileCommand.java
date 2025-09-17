package net.fantasydreams.itemsAdderLobFile.commands;

import net.fantasydreams.itemsAdderLobFile.ItemsAdderLobFile;
import net.fantasydreams.itemsAdderLobFile.api.LobFileAPI;
import net.fantasydreams.itemsAdderLobFile.config.ItemsAdderConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LobFileCommand implements CommandExecutor {
    private final ItemsAdderLobFile plugin;
    private final LobFileAPI lobFileAPI;
    private final ItemsAdderConfigUpdater configUpdater;

    public LobFileCommand(ItemsAdderLobFile plugin, LobFileAPI lobFileAPI, ItemsAdderConfigUpdater configUpdater) {
        this.plugin = plugin;
        this.lobFileAPI = lobFileAPI;
        this.configUpdater = configUpdater;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lobfile.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "check":
                checkForUpdates(sender);
                break;
            case "status":
                showStatus(sender);
                break;
            case "test":
                testConnection(sender);
                break;
            case "reload":
                reloadConfig(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== LobFile Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/lobfile check" + ChatColor.WHITE + " - Check for pack updates now");
        sender.sendMessage(ChatColor.YELLOW + "/lobfile status" + ChatColor.WHITE + " - Show current status");
        sender.sendMessage(ChatColor.YELLOW + "/lobfile test" + ChatColor.WHITE + " - Test API connection");
        sender.sendMessage(ChatColor.YELLOW + "/lobfile reload" + ChatColor.WHITE + " - Reload plugin configuration");
    }

    private void checkForUpdates(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Checking for pack updates...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String latestUrl = lobFileAPI.getLatestPackDownloadUrl();
                String currentUrl = configUpdater.getCurrentExternalHostUrl();

                if (latestUrl != null && !latestUrl.equals(currentUrl)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (configUpdater.updateExternalHostUrl(latestUrl)) {
                            sender.sendMessage(ChatColor.GREEN + "Updated ItemsAdder URL to: " + latestUrl);

                            if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iazip");
                                sender.sendMessage(ChatColor.GREEN + "Triggered ItemsAdder resource pack regeneration");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Failed to update ItemsAdder configuration");
                        }
                    });
                } else {
                    sender.sendMessage(ChatColor.GREEN + "ItemsAdder URL is already up to date");
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error checking for updates: " + e.getMessage());
            }
        });
    }

    private void showStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== LobFile Status ===");
        sender.sendMessage(ChatColor.YELLOW + "ItemsAdder Present: " +
            (configUpdater.isItemsAdderPresent() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));

        String currentUrl = configUpdater.getCurrentExternalHostUrl();
        sender.sendMessage(ChatColor.YELLOW + "Current URL: " +
            (currentUrl != null ? ChatColor.WHITE + currentUrl : ChatColor.RED + "None"));

        sender.sendMessage(ChatColor.YELLOW + "Check Interval: " + ChatColor.WHITE +
            plugin.getSecretConfig().getInt("lobfile.check-interval-minutes", 1) + " minute(s)");
    }

    private void testConnection(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Testing LobFile API connection...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean connected = lobFileAPI.testConnection();
            sender.sendMessage(connected ?
                ChatColor.GREEN + "Connection successful!" :
                ChatColor.RED + "Connection failed!");
        });
    }

    private void reloadConfig(CommandSender sender) {
        plugin.reloadConfig();
        plugin.loadConfiguration();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
    }
}