package com.kingssmp.rituals;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final KingsSMPPlugin plugin;
    private final Map<String, Long> cd = new HashMap<>();
    public CooldownManager(KingsSMPPlugin plugin) { this.plugin = plugin; }

    public boolean ready(Player p, ItemManager.KSItem item) {
        String key = p.getUniqueId() + ":" + item.name();
        long now = System.currentTimeMillis();
        Long until = cd.get(key);
        return until == null || now >= until;
    }

    public long remainingMs(Player p, ItemManager.KSItem item) {
        String key = p.getUniqueId() + ":" + item.name();
        Long until = cd.get(key);
        long now = System.currentTimeMillis();
        return Math.max(0, (until == null ? 0 : until - now));
    }

    public void start(Player p, ItemManager.KSItem item, int seconds) {
        String key = p.getUniqueId() + ":" + item.name();
        cd.put(key, System.currentTimeMillis() + seconds * 1000L);
    }
}
