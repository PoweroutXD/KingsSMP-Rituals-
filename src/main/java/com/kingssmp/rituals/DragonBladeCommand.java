package com.kingssmp.rituals;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DragonBladeCommand implements CommandExecutor {

    private final AbilityManager abilities;
    private final ItemManager items;

    public DragonBladeCommand(AbilityManager abilities, ItemManager items) {
        this.abilities = abilities;
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
        if (args.length != 1) {
            p.sendMessage(ChatColor.YELLOW + "Usage: /dragonblade <breath|ride>");
            return true;
        }
        var held = p.getInventory().getItemInMainHand();
        if (!items.is(held, ItemManager.KSItem.DRAGON_BONE_BLADE)) {
            p.sendMessage(ChatColor.RED + "Hold the Dragon Bone Blade to use this command.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "breath" -> abilities.activateDragonBreath(p);
            case "ride" -> abilities.activateRideDragon(p);
            default -> p.sendMessage(ChatColor.YELLOW + "Usage: /dragonblade <breath|ride>");
        }
        return true;
    }
}
