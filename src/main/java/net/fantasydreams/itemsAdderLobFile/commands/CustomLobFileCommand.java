package net.fantasydreams.itemsAdderLobFile.commands;

import net.fantasydreams.itemsAdderLobFile.ItemsAdderLobFile;
import net.fantasydreams.itemsAdderLobFile.api.LobFileAPI;
import net.fantasydreams.itemsAdderLobFile.config.ItemsAdderConfigUpdater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CustomLobFileCommand extends Command {
    private final LobFileCommand lobFileCommand;

    public CustomLobFileCommand(ItemsAdderLobFile plugin, LobFileAPI lobFileAPI, ItemsAdderConfigUpdater configUpdater) {
        super("lobfile");
        this.lobFileCommand = new LobFileCommand(plugin, lobFileAPI, configUpdater);
        this.setDescription("Manage LobFile integration");
        this.setUsage("/lobfile <check|status|test|reload>");
        this.setPermission("lobfile.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return lobFileCommand.onCommand(sender, this, commandLabel, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("check", "status", "test", "reload");
        }
        return super.tabComplete(sender, alias, args);
    }
}