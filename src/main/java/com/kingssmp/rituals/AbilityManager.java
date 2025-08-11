package com.kingssmp.rituals;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class AbilityManager {

    private final KingsSMPPlugin plugin;
    private final CooldownManager cooldowns;
    private final RitualManager rituals;
    private final ItemManager items;

    private final Map<UUID, Location> voidBowMarked = new HashMap<>();

    public AbilityManager(KingsSMPPlugin plugin, CooldownManager cooldowns, RitualManager rituals, ItemManager items) {
        this.plugin = plugin;
        this.cooldowns = cooldowns;
        this.rituals = rituals;
        this.items = items;
    }

    public void activate(Player p, ItemManager.KSItem type) {
        switch (type) {
            case DRAGON_BONE_BLADE -> dragonBreath(p);
            case AVIAN_TRIDENT -> grantFlight(p);
            case VOID_BOW -> p.sendMessage(Component.text("Use the bow: first arrow marks a location; second arrow teleports you or the hit entity. Cooldown 10s."));
            case KINGS_CROWN, LIFE_STEALER, BUCKET_OF_MULK -> p.sendMessage(Component.text("This item has a passive effect."));
        }
    }

    private void showActionBarCountdown(Player p, int seconds, String label) {
        new BukkitRunnable() {
            int t = seconds;
            @Override public void run() {
                if (!p.isOnline()) { cancel(); return; }
                int m = t / 60, s = t % 60;
                p.sendActionBar(Component.text(label + " " + m + ":" + String.format("%02d", s)));
                t--;
                if (t < 0) cancel();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void dragonBreath(Player p) {
        int cd = plugin.getConfig().getInt("cooldowns.DRAGON_BONE_BLADE", 300);
        int dur = plugin.getConfig().getInt("abilities.DRAGON_BONE_BLADE-duration", 300);
        if (!cooldowns.ready(p, ItemManager.KSItem.DRAGON_BONE_BLADE)) {
            long ms = cooldowns.remainingMs(p, ItemManager.KSItem.DRAGON_BONE_BLADE);
            p.sendMessage("On cooldown for " + (ms/1000) + "s");
            return;
        }
        cooldowns.start(p, ItemManager.KSItem.DRAGON_BONE_BLADE, cd);
        showActionBarCountdown(p, dur, "Dragon Breath");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

        // For duration, allow right-click to spew clouds forward every 2s
        new BukkitRunnable() {
            int ticks = dur;
            @Override public void run() {
                if (ticks <= 0 || !p.isOnline()) { cancel(); return; }
                // Emit lingering damage cloud ahead
                Location eye = p.getEyeLocation();
                Vector dir = eye.getDirection();
                Location spawn = eye.add(dir.multiply(3));
                AreaEffectCloud cloud = (AreaEffectCloud) p.getWorld().spawnEntity(spawn, EntityType.AREA_EFFECT_CLOUD);
                cloud.setParticle(Particle.DRAGON_BREATH);
                cloud.setRadius(3f);
                cloud.setDuration(40);
                cloud.setReapplicationDelay(10);
                cloud.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1, true, false, false), true);
                ticks -= 2;
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }

    private void grantFlight(Player p) {
        int cd = plugin.getConfig().getInt("cooldowns.AVIAN_TRIDENT", 180);
        int dur = plugin.getConfig().getInt("abilities.AVIAN_TRIDENT-duration", 180);
        if (!cooldowns.ready(p, ItemManager.KSItem.AVIAN_TRIDENT)) {
            long ms = cooldowns.remainingMs(p, ItemManager.KSItem.AVIAN_TRIDENT);
            p.sendMessage("On cooldown for " + (ms/1000) + "s");
            return;
        }
        cooldowns.start(p, ItemManager.KSItem.AVIAN_TRIDENT, cd);
        showActionBarCountdown(p, dur, "Flight");
        p.setAllowFlight(true);
        p.setFlying(true);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1f, 0.5f);
        new BukkitRunnable() {
            int t = dur;
            @Override public void run() {
                if (t-- <= 0 || !p.isOnline()) {
                    if (p.isOnline()) { p.setFlying(false); p.setAllowFlight(false); }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void markVoidLocation(Player p, Location loc) {
        voidBowMarked.put(p.getUniqueId(), loc.clone());
        p.sendActionBar(Component.text("Location marked."));
    }

    public boolean tryTeleportOnVoid(Player shooter, LivingEntity hit) {
        // If a location marked exists, teleport shooter or entity there, start cooldown
        Location mark = voidBowMarked.get(shooter.getUniqueId());
        if (mark == null) return false;
        int cd = plugin.getConfig().getInt("cooldowns.VOID_BOW", 10);
        if (!cooldowns.ready(shooter, ItemManager.KSItem.VOID_BOW)) {
            shooter.sendMessage("Void Bow on cooldown " + (cooldowns.remainingMs(shooter, ItemManager.KSItem.VOID_BOW)/1000) + "s");
            return true;
        }
        cooldowns.start(shooter, ItemManager.KSItem.VOID_BOW, cd);
        if (hit != null) {
            hit.teleport(mark);
            shooter.sendActionBar(Component.text("Teleported target."));
        } else {
            shooter.teleport(mark);
            shooter.sendActionBar(Component.text("Teleported."));
        }
        shooter.getWorld().playSound(mark, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        voidBowMarked.remove(shooter.getUniqueId());
        return true;
    
    public void activateDragonBreath(Player p) { dragonBreath(p); }

    public void activateRideDragon(Player p) { rideDragon(p); }


    private void rideDragon(Player p) {
        int cd = plugin.getConfig().getInt("cooldowns.DRAGON_BONE_BLADE", 300);
        int dur = plugin.getConfig().getInt("abilities.DRAGON_BONE_BLADE-duration", 300);
        if (!cooldowns.ready(p, ItemManager.KSItem.DRAGON_BONE_BLADE)) {
            long ms = cooldowns.remainingMs(p, ItemManager.KSItem.DRAGON_BONE_BLADE);
            p.sendMessage("On cooldown for " + (ms/1000) + "s");
            return;
        }
        cooldowns.start(p, ItemManager.KSItem.DRAGON_BONE_BLADE, cd);
        showActionBarCountdown(p, dur, "Ride Dragon");
        // Safe, no-grief 'rideable dragon': AI-disabled EnderDragon steered by player's look direction
        var world = p.getWorld();
        world.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        var dragon = (org.bukkit.entity.EnderDragon) world.spawnEntity(p.getLocation().add(0, 2, 0), org.bukkit.entity.EntityType.ENDER_DRAGON);
        dragon.setAI(false);
        dragon.setInvulnerable(true);
        dragon.setCustomName(net.kyori.adventure.text.Component.text("Summoned Dragon"));
        dragon.setCustomNameVisible(true);
        dragon.setRemoveWhenFarAway(false);
        dragon.setSilent(true);
        dragon.addPassenger(p);

        new org.bukkit.scheduler.BukkitRunnable() {
            int t = dur;
            @Override public void run() {
                if (t-- <= 0 || !p.isOnline() || dragon.isDead() || !dragon.isValid()) {
                    if (p.isInsideVehicle() && p.getVehicle() == dragon) {
                        dragon.removePassenger(p);
                    }
                    dragon.remove();
                    cancel();
                    return;
                }
                // steer: follow player's look direction
                org.bukkit.Location loc = dragon.getLocation();
                org.bukkit.util.Vector dir = p.getLocation().getDirection().normalize();
                double speed = 0.8; // blocks/tick-ish
                org.bukkit.Location next = loc.add(dir.multiply(speed));
                next.setYaw(p.getLocation().getYaw());
                next.setPitch(p.getLocation().getPitch());
                dragon.teleport(next);
                // particles
                world.spawnParticle(org.bukkit.Particle.DRAGON_BREATH, dragon.getLocation().add(0, -1, 0), 6, 0.4, 0.2, 0.4, 0.0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
