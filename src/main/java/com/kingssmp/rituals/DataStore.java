package com.kingssmp.rituals;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class DataStore {
    private final KingsSMPPlugin plugin;
    private final File file;
    private final FileConfiguration cfg;
    private final Map<ItemManager.KSItem, Boolean> crafted = new EnumMap<>(ItemManager.KSItem.class);

    public DataStore(KingsSMPPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);

        for (ItemManager.KSItem it : ItemManager.KSItem.values()) {
            if (it == ItemManager.KSItem.BUCKET_OF_MULK) continue;
            boolean v = cfg.getBoolean("crafted." + it.name(), false);
            crafted.put(it, v);
        }
    }

    public boolean isCrafted(ItemManager.KSItem it) {
        if (it == ItemManager.KSItem.BUCKET_OF_MULK) return false;
        return crafted.getOrDefault(it, false);
    }

    public void markCrafted(ItemManager.KSItem it) {
        if (it == ItemManager.KSItem.BUCKET_OF_MULK) return;
        crafted.put(it, true);
        cfg.set("crafted." + it.name(), true);
        save();
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save data.yml: " + e.getMessage());
        }
    }
}
