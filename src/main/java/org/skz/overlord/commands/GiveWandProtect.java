package org.skz.overlord.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class GiveWandProtect implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.isOp()) {
            Player player = (Player) sender;
            giveProtectionAxe(player);
            player.sendMessage(ChatColor.GREEN + "You have received a protection axe");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return false;
        }
    }

    public void giveProtectionAxe(Player player) {
        ItemStack axe = new ItemStack(Material.STONE_AXE);
        ItemMeta meta = axe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Protection Axe");
            meta.setLore(Arrays.asList("This axe is for protecting regions."));
            axe.setItemMeta(meta);
        }
        player.getInventory().addItem(axe);
    }
}
