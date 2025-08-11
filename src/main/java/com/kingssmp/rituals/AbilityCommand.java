package com.kingssmp.rituals;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityCommand implements CommandExecutor {

    private final AbilityManager abilityManager;
    private final ItemManager items;

    public AbilityCommand(AbilityManager abilityManager, ItemManager items) {
        this.abilityManager = abilityManager;
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
        var held = p.getInventory().getItemInMainHand();
        var which = items.which(held);
        if (which.isEmpty()) { p.sendMessage("Hold a Kings SMP item."); return true; }
        abilityManager.activate(p, which.get());
        return true;
    }
}
