package com.kingssmp.rituals;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RitualManager {

    private final KingsSMPPlugin plugin;
    private final Set<BukkitTask> tasks = new HashSet<>();

    public RitualManager(KingsSMPPlugin plugin) { this.plugin = plugin; }

    public void startRitual(Player crafter, ItemStack item, int durationSeconds, Runnable onFinish) {
        Location base = crafter.getLocation().clone();
        World world = base.getWorld();
        if (world == null) return;
        base = base.add(0, 2.0, 0);

        // Display: floating spinning item via ItemDisplay (1.19+), fallback armor stand if needed
        ItemDisplay display = world.spawn(base, ItemDisplay.class, d -> {
            d.setItemStack(item);
            d.setRotation(0f, 0f);
            d.setBillboard(ItemDisplay.Billboard.CENTER);
        });

        BossBar bar = BossBar.bossBar(Component.text("Ritual in progress..."), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        // Show to all players
        for (Player p : Bukkit.getOnlinePlayers()) p.showBossBar(bar);

        long start = System.currentTimeMillis();
        long total = durationSeconds * 1000L;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            float yaw = 0;
            @Override public void run() {
                long elapsed = System.currentTimeMillis() - start;
                float progress = Math.max(0f, 1f - (float)elapsed/total);
                bar.progress(progress);
                bar.name(Component.text(String.format("Ritual ends in %d:%02d  @ %.1f, %.1f, %.1f",
                        (int)((total - elapsed)/1000)/60,
                        (int)((total - elapsed)/1000)%60,
                        base.getX(), base.getY(), base.getZ())));
                // spin
                yaw += 6f;
                display.setRotation(yaw, 0f);

                if (elapsed >= total) {
                    // finish
                    for (Player p : Bukkit.getOnlinePlayers()) p.hideBossBar(bar);
                    display.remove();
                    world.dropItemNaturally(base, item);
                    onFinish.run();
                    cancelSelf(this);
                }
            }
        }, 0L, 10L);
        tasks.add(task);
    }

    private void cancelSelf(Runnable r) {
        // find the task and cancel
        for (BukkitTask t : tasks) {
            if (t.getOwner().equals(plugin) && !t.isCancelled()) {
                // best-effort cancel all
                t.cancel();
            }
        }
        tasks.clear();
    }

    public void shutdown() {
        for (BukkitTask t : tasks) t.cancel();
        tasks.clear();
    }
}
