package org.skz.overlord.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.skz.overlord.Overlord;
import org.skz.overlord.other.CustomBlockData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BLoadCommand implements CommandExecutor, TabCompleter {

    private final Overlord plugin;

    public BLoadCommand(Overlord plugin) {
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
            player.sendMessage("§cUsage: /bload <name>");
            return true;
        }

        String saveName = args[0];
        File saveFolder = new File(plugin.getDataFolder(), "saves");

        File saveFile = new File(saveFolder, saveName + ".ovr");
        if (!saveFile.exists()) {
            player.sendMessage("§cThe save '" + saveName + "' does not exist.");
            return true;
        }

        try (Scanner scanner = new Scanner(saveFile)) {
            if (!scanner.hasNextLine()) {
                player.sendMessage("§cInvalid save file.");
                return true;
            }

            String versionLine = scanner.nextLine();
            if (!versionLine.startsWith("version:")) {
                player.sendMessage("§cInvalid save file: missing version.");
                return true;
            }

            String savedVersion = versionLine.split(":")[1];
            String currentVersion = plugin.getDescription().getVersion();

            if (!savedVersion.equals(currentVersion)) {
                player.sendMessage("§cIncompatible version. Save: " + savedVersion + ", Plugin: " + currentVersion);
                return true;
            }

            if (!scanner.hasNextLine()) {
                player.sendMessage("§cInvalid save file: missing origin.");
                return true;
            }

            String originLine = scanner.nextLine();
            if (!originLine.startsWith("origin:")) {
                player.sendMessage("§cInvalid save file: missing origin.");
                return true;
            }

            String[] originParts = originLine.split(":")[1].split(",");
            int originX = Integer.parseInt(originParts[0]);
            int originY = Integer.parseInt(originParts[1]);
            int originZ = Integer.parseInt(originParts[2]);

            Location currentFirstPoint = plugin.getFirstPoint();
            if (currentFirstPoint == null) {
                player.sendMessage("§cYou must first set point 1 using a selection tool.");
                return true;
            }

            World world = currentFirstPoint.getWorld();
            List<CustomBlockData> placedBlocks = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    try {
                        int relX = Integer.parseInt(parts[0]);
                        int relY = Integer.parseInt(parts[1]);
                        int relZ = Integer.parseInt(parts[2]);
                        Material material = Material.valueOf(parts[3]);

                        Location blockLocation = currentFirstPoint.clone().add(relX, relY, relZ);

                        Block block = blockLocation.getBlock();
                        placedBlocks.add(new CustomBlockData(block.getLocation(), block.getType(), block.getBlockData()));

                        block.setType(material);

                        if (parts.length > 4) {
                            String blockData = parts[4];
                            block.setBlockData(Bukkit.createBlockData(blockData));
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            plugin.addUndoAction(placedBlocks);

            player.sendMessage("§aStructure successfully loaded!");
        } catch (FileNotFoundException e) {
            player.sendMessage("§cError loading: file not found.");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            File saveFolder = new File(plugin.getDataFolder(), "saves");
            if (saveFolder.exists()) {
                File[] files = saveFolder.listFiles((dir, name) -> name.endsWith(".ovr"));
                if (files != null) {
                    for (File file : files) {
                        String fileName = file.getName().replace(".ovr", "");
                        completions.add(fileName);
                    }
                }
            }
        }

        return completions;
    }
}
