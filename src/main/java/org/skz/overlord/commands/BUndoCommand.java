package org.skz.overlord.commands;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.skz.overlord.Overlord;
import org.skz.overlord.other.CustomBlockData;

import java.util.ArrayList;
import java.util.List;

public class BUndoCommand implements CommandExecutor {

    private final Overlord plugin;

    public BUndoCommand(Overlord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        List<List<CustomBlockData>> undoHistory = plugin.getUndoHistory();
        if (undoHistory.isEmpty()) {
            player.sendMessage("§cNo actions to undo.");
            return true;
        }

        int undoCount = 1;
        if (args.length > 0) {
            try {
                undoCount = Integer.parseInt(args[0]);
                undoCount = Math.min(undoCount, 10);
                undoCount = Math.min(undoCount, undoHistory.size());
            } catch (NumberFormatException e) {
                player.sendMessage("§cUsage: /bundo [number]");
                return true;
            }
        }

        for (int i = 0; i < undoCount; i++) {
            List<CustomBlockData> lastAction = undoHistory.remove(undoHistory.size() - 1);
            for (CustomBlockData data : lastAction) {
                Block block = data.getLocation().getBlock();
                block.setType(data.getMaterial());
                block.setBlockData(data.getBlockData());
            }
        }

        player.sendMessage("§a" + undoCount + " action(s) successfully undone!");
        return true;
    }
}
