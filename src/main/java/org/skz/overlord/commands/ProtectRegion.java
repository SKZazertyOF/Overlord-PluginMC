package org.skz.overlord.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.skz.overlord.Overlord;
import org.skz.overlord.other.ProtectedRegion;

public class ProtectRegion implements CommandExecutor {

    private final Overlord plugin;

    public ProtectRegion(Overlord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
            return true;
        }

        if (plugin.getFirstPoint() == null || plugin.getSecondPoint() == null) {
            player.sendMessage(ChatColor.RED + "You must first set two points using the protection axe.");
            return true;
        }

        // Generate a default name if no name is provided
        String regionName = (args.length > 0) ? args[0] : "Region" + (plugin.getProtectedRegions().size() + 1);

        // Create a new protected region
        ProtectedRegion region = new ProtectedRegion(regionName, plugin.getFirstPoint(), plugin.getSecondPoint());
        plugin.addProtectedRegion(region);

        player.sendMessage(ChatColor.GREEN + "New protected region '" + regionName + "' added!");
        return true;
    }
}