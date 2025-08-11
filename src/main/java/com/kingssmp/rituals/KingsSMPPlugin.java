package com.kingssmp.rituals;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class KingsSMPPlugin extends JavaPlugin {

    private static KingsSMPPlugin instance;
    private ItemManager itemManager;
    private RitualManager ritualManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldowns;
    private DataStore dataStore;

    public static KingsSMPPlugin get() { return instance; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("data.yml", false);

        dataStore = new DataStore(this);
        cooldowns = new CooldownManager(this);
        itemManager = new ItemManager(this);
        ritualManager = new RitualManager(this);
        abilityManager = new AbilityManager(this, cooldowns, ritualManager, itemManager);

        // Register command
        getCommand("ksmpgive").setExecutor(new KSMPGiveCommand(itemManager));
        getCommand("dragonblade").setExecutor(new DragonBladeCommand(abilityManager, itemManager));
        getCommand("ksmpgive").setExecutor(new KSMPGiveCommand(itemManager));
    
        getCommand("ability").setExecutor(new AbilityCommand(abilityManager, itemManager));

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new CraftListener(itemManager, ritualManager, dataStore), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(itemManager), this);
        Bukkit.getPluginManager().registerEvents(new BowListener(itemManager, abilityManager), this);
        Bukkit.getPluginManager().registerEvents(new ConsumeListener(itemManager), this);
        Bukkit.getPluginManager().registerEvents(new WearListener(itemManager), this);

        getLogger().info("KingsSMP-Rituals enabled.");
    }

    @Override
    public void onDisable() {
        if (ritualManager != null) ritualManager.shutdown();
        if (dataStore != null) dataStore.save();
    }

    public NamespacedKey key(String id) {
        return new NamespacedKey(this, id);
    }

    public DataStore getDataStore() { return dataStore; }
}
