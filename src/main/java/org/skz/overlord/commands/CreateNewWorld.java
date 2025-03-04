package org.skz.overlord.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import java.util.Random;

public class CreateNewWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /createworld <worldname>");
            return false;
        }

        String worldName = args[0];

        if (sender instanceof Player) {
            Player player = (Player) sender;

            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.generator(new ChunkGenerator() {
                @Override
                public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
                    ChunkData chunkData = createChunkData(world);

                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            chunkData.setBlock(x, 0, z, Material.BEDROCK);
                        }
                    }

                    return chunkData;
                }
            });

            World newWorld = worldCreator.createWorld();

            Location spawnLocation = new Location(newWorld, 0, 2, 0);
            player.teleport(spawnLocation);

            // Send success messages to the player
            player.sendMessage(ChatColor.GREEN + "The world " + worldName + " has been created successfully!");
            player.sendMessage(ChatColor.GREEN + "You have been teleported to coordinates (0, 1, 0).");
            player.sendMessage(ChatColor.DARK_AQUA + "To return to the overworld, use /execute in minecraft:overworld run tp {username} 0 500 0");

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return false;
        }
    }
}