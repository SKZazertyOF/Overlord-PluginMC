package org.skz.overlord.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.skz.overlord.Overlord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BSaveCommand implements CommandExecutor {

    private final Overlord plugin;

    public BSaveCommand(Overlord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§cUsage: /bsave <name>");
            return true;
        }

        String saveName = args[0];
        File saveFolder = new File(plugin.getDataFolder(), "saves");

        if (!saveFolder.exists()) {
            if (!saveFolder.mkdirs()) {
                player.sendMessage("§cError: Unable to create save folder.");
                return true;
            }
        }

        File saveFile = new File(saveFolder, saveName + ".ovr");

        try (FileWriter writer = new FileWriter(saveFile)) {
            Location firstPoint = plugin.getFirstPoint();
            Location secondPoint = plugin.getSecondPoint();

            if (firstPoint == null || secondPoint == null) {
                player.sendMessage("§cYou must select an area using a selection tool.");
                return true;
            }

            String pluginVersion = plugin.getDescription().getVersion();
            writer.write("version:" + pluginVersion + "\n");

            writer.write("origin:" + firstPoint.getBlockX() + "," + firstPoint.getBlockY() + "," + firstPoint.getBlockZ() + "\n");

            for (int x = Math.min(firstPoint.getBlockX(), secondPoint.getBlockX()); x <= Math.max(firstPoint.getBlockX(), secondPoint.getBlockX()); x++) {
                for (int y = Math.min(firstPoint.getBlockY(), secondPoint.getBlockY()); y <= Math.max(firstPoint.getBlockY(), secondPoint.getBlockY()); y++) {
                    for (int z = Math.min(firstPoint.getBlockZ(), secondPoint.getBlockZ()); z <= Math.max(firstPoint.getBlockZ(), secondPoint.getBlockZ()); z++) {
                        Block block = player.getWorld().getBlockAt(x, y, z);

                        if (block.getType() != Material.AIR) {
                            int relX = x - firstPoint.getBlockX();
                            int relY = y - firstPoint.getBlockY();
                            int relZ = z - firstPoint.getBlockZ();

                            writer.write(relX + "," + relY + "," + relZ + "," + block.getType().name() + "," + block.getBlockData().getAsString() + "\n");
                        }
                    }
                }
            }

            player.sendMessage("§aSave completed: " + saveName + ".ovr");
        } catch (IOException e) {
            player.sendMessage("§cError during save: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
