package net.fantasydreams.itemsAdderLobFile.config;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class ItemsAdderConfigUpdater {
    private final File itemsAdderConfigFile;

    public ItemsAdderConfigUpdater() {
        this.itemsAdderConfigFile = new File("plugins/ItemsAdder/config.yml");
    }

    public boolean updateExternalHostUrl(String newUrl) {
        if (!itemsAdderConfigFile.exists()) {
            return false;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(itemsAdderConfigFile);

            config.set("resource-pack.hosting.external-host.enabled", true);
            config.set("resource-pack.hosting.external-host.url", newUrl);

            config.set("resource-pack.hosting.lobfile.enabled", false);
            config.set("resource-pack.hosting.self-host.enabled", false);
            config.set("resource-pack.hosting.no-host.enabled", false);

            config.save(itemsAdderConfigFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCurrentExternalHostUrl() {
        if (!itemsAdderConfigFile.exists()) {
            return null;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(itemsAdderConfigFile);
            return config.getString("resource-pack.hosting.external-host.url", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isItemsAdderPresent() {
        return itemsAdderConfigFile.exists();
    }
}