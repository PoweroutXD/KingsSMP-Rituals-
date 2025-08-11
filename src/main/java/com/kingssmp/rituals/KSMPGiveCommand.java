package com.kingssmp.rituals;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class KSMPGiveCommand implements CommandExecutor {

    private final ItemManager items;

    public KSMPGiveCommand(ItemManager items) {
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (!p.hasPermission("kingssmp.admin")) {
            p.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        if (args.length != 1) {
            p.sendMessage(ChatColor.YELLOW + "Usage: /ksmpgive <item>");
            return true;
        }
        try {
            ItemManager.KSItem type = ItemManager.KSItem.valueOf(args[0].toUpperCase());
            p.getInventory().addItem(items.get(type));
            p.sendMessage(ChatColor.GREEN + "Gave you a " + type.display);
        } catch (IllegalArgumentException e) {
            p.sendMessage(ChatColor.RED + "Unknown item. Valid: DRAGON_BONE_BLADE, LIFE_STEALER, AVIAN_TRIDENT, VOID_BOW, KINGS_CROWN, BUCKET_OF_MULK");
        }
        return true;
    }
}
