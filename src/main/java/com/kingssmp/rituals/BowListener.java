package com.kingssmp.rituals;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class BowListener implements Listener {

    private final ItemManager items;
    private final AbilityManager abilities;

    public BowListener(ItemManager items, AbilityManager abilities) {
        this.items = items;
        this.abilities = abilities;
    }

    @EventHandler
    public void onVoidMark(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow arrow)) return;
        ProjectileSource src = arrow.getShooter();
        if (!(src instanceof Player p)) return;
        var held = p.getInventory().getItemInMainHand();
        if (!items.is(held, ItemManager.KSItem.VOID_BOW)) return;

        // If it hit a block, mark location
        if (e.getHitBlock() != null) {
            Location loc = arrow.getLocation();
            abilities.markVoidLocation(p, loc);
        }
    }

    @EventHandler
    public void onVoidTeleport(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow arrow)) return;
        ProjectileSource src = arrow.getShooter();
        if (!(src instanceof Player p)) return;
        var held = p.getInventory().getItemInMainHand();
        if (!items.is(held, ItemManager.KSItem.VOID_BOW)) return;

        Entity hit = e.getEntity();
        abilities.tryTeleportOnVoid(p, (hit instanceof LivingEntity le) ? le : null);
    }
}
