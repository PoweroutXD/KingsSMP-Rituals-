package com.kingssmp.rituals;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    private final ItemManager items;
    private final RitualManager rituals;
    private final DataStore data;

    public CraftListener(ItemManager items, RitualManager rituals, DataStore data) {
        this.items = items;
        this.rituals = rituals;
        this.data = data;
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ItemStack result = e.getRecipe().getResult();
        var which = items.which(result);
        if (which.isEmpty()) return;

        var type = which.get();
        if (type == ItemManager.KSItem.BUCKET_OF_MULK) {
            // allow crafting, add sound when consumed elsewhere
            return;
        }

        // one-per-server guard
        if (data.isCrafted(type)) {
            e.setCancelled(true);
            p.sendMessage(Component.text("This legendary item has already been forged on this server."));
            return;
        }

        // consume ingredients and cancel giving the item; start ritual
        e.setCancelled(true);
        e.getInventory().setResult(new ItemStack(Material.AIR));

        int seconds = p.getServer().getPluginManager().getPlugin("KingsSMP-Rituals").getConfig().getInt("ritual-duration-seconds", 1800);
        ItemStack prize = items.get(type);
        rituals.startRitual(p, prize, seconds, () -> {
            data.markCrafted(type);
            Bukkit.broadcast(Component.text(type.name() + " ritual completed! The item has spawned."));
            p.getWorld().playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        });
        Bukkit.broadcast(Component.text(p.getName() + " has begun the " + type.name() + " ritual!"));
    }
}
