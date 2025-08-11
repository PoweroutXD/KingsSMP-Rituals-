package com.kingssmp.rituals;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ConsumeListener implements Listener {

    private final ItemManager items;
    public ConsumeListener(ItemManager items) { this.items = items; }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        ItemStack it = e.getItem();
        if (!items.is(it, ItemManager.KSItem.BUCKET_OF_MULK)) return;
        Player p = e.getPlayer();
        // Better than golden carrot: restore lots of hunger/saturation
        p.setFoodLevel(Math.min(20, p.getFoodLevel() + 10));
        p.setSaturation(Math.min(20f, p.getSaturation() + 12f));
        p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
    }
}
