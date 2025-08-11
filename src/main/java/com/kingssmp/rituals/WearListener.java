package com.kingssmp.rituals;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WearListener implements Listener {

    private final ItemManager items;
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();

    public WearListener(ItemManager items) { this.items = items; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) { startLoop(e.getPlayer()); }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) { stopLoop(e.getPlayer()); }

    private void startLoop(Player p) {
        stopLoop(p);
        var task = Bukkit.getScheduler().runTaskTimer(KingsSMPPlugin.get(), () -> {
            ItemStack helm = p.getInventory().getHelmet();
            if (items.is(helm, ItemManager.KSItem.KINGS_CROWN)) {
                // Apply permanent effects while worn
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 2, true, false, false)); // Strength III = amplifier 2
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, true, false, false)); // Resistance I
            }
        }, 0L, 40L);
        tasks.put(p.getUniqueId(), task);
    }

    private void stopLoop(Player p) {
        var t = tasks.remove(p.getUniqueId());
        if (t != null) t.cancel();
    }
}
