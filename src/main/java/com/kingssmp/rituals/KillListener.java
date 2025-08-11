package com.kingssmp.rituals;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class KillListener implements Listener {

    private final ItemManager items;

    public KillListener(ItemManager items) { this.items = items; }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        if (!(e.getEntity().getKiller() instanceof Player killer)) return;
        var held = killer.getInventory().getItemInMainHand();
        if (!items.is(held, ItemManager.KSItem.LIFE_STEALER)) return;

        double baseMax = 20.0;
        double currentMax = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double targetMax = Math.min(28.0, currentMax + 4.0); // +2 hearts (4 health), max 14 hearts (28 health)
        killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(targetMax);
        killer.setHealth(Math.min(killer.getHealth() + 4.0, targetMax));
    }
}
