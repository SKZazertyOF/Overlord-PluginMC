package org.skz.overlord.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.skz.overlord.Overlord;
import org.skz.overlord.other.ProtectedRegion;

import java.util.*;
import java.util.stream.Collectors;

public class RegionCommand implements CommandExecutor, TabCompleter {

    private final Overlord plugin;
    private final Map<UUID, BukkitTask> particleTasks = new HashMap<>();

    public RegionCommand(Overlord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /region <del|see|modify|list|info>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "del":
                return handleDelete(player, args);
            case "see":
                return handleSee(player, args);
            case "modify":
                return handleModify(player, args);
            case "list":
                return handleList(player);
            case "info":
                return handleInfo(player, args);
            case "reload":
                return handleReload(sender);
            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /region <del|see|modify|list|info>");
                return true;
        }
    }

    private boolean handleList(Player player) {
        List<ProtectedRegion> regions = plugin.getProtectedRegions();

        if (regions.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No regions found.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "List of protected regions:");
        for (ProtectedRegion region : regions) {
            player.sendMessage(ChatColor.YELLOW + "- " + region.getName());
        }

        return true;
    }

    private boolean handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /region info <name>");
            return true;
        }

        String regionName = args[1];
        ProtectedRegion targetRegion = null;

        for (ProtectedRegion region : plugin.getProtectedRegions()) {
            if (region.getName().equalsIgnoreCase(regionName)) {
                targetRegion = region;
                break;
            }
        }

        if (targetRegion == null) {
            player.sendMessage(ChatColor.RED + "Region '" + regionName + "' not found.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Info for region: " + ChatColor.YELLOW + targetRegion.getName());
        player.sendMessage(ChatColor.GREEN + "PvP: " + (targetRegion.isPvpAllowed() ? "Enabled" : "Disabled"));
        player.sendMessage(ChatColor.GREEN + "Build: " + (targetRegion.isBuildAllowed() ? "Enabled" : "Disabled"));
        player.sendMessage(ChatColor.GREEN + "Break Block: " + (targetRegion.isBreakBlockAllowed() ? "Enabled" : "Disabled"));
        player.sendMessage(ChatColor.GREEN + "TNT Explosions: " + (targetRegion.isExplosionTNTAllowed() ? "Enabled" : "Disabled"));
        player.sendMessage(ChatColor.GREEN + "Creeper Explosions: " + (targetRegion.isExplosionCreeperAllowed() ? "Enabled" : "Disabled"));

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
            return true;
        }

        plugin.reloadProtectedRegions();
        sender.sendMessage(ChatColor.GREEN + "Overlord configuration reloaded.");
        return true;
    }



    private boolean handleDelete(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /region del <name>");
            return true;
        }

        String regionName = args[1];
        List<ProtectedRegion> regions = plugin.getProtectedRegions();

        ProtectedRegion regionToRemove = null;
        for (ProtectedRegion region : regions) {
            if (region.getName().equalsIgnoreCase(regionName)) {
                regionToRemove = region;
                break;
            }
        }

        if (regionToRemove == null) {
            player.sendMessage(ChatColor.RED + "Region '" + regionName + "' not found.");
            return true;
        }

        regions.remove(regionToRemove);
        plugin.saveProtectedRegions();
        player.sendMessage(ChatColor.GREEN + "Region '" + regionName + "' has been deleted.");
        return true;
    }

    private boolean handleSee(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /region see <name|off>");
            return true;
        }

        String regionName = args[1].toLowerCase();

        if (regionName.equals("off")) {
            stopParticles(player);
            player.sendMessage(ChatColor.GREEN + "Particles have been turned off.");
            return true;
        }

        ProtectedRegion targetRegion = null;
        for (ProtectedRegion region : plugin.getProtectedRegions()) {
            if (region.getName().equalsIgnoreCase(regionName)) {
                targetRegion = region;
                break;
            }
        }

        if (targetRegion == null) {
            player.sendMessage(ChatColor.RED + "Region '" + regionName + "' not found.");
            return true;
        }

        stopParticles(player);
        startParticles(player, targetRegion);
        player.sendMessage(ChatColor.GREEN + "Displaying particles for region: " + regionName);
        return true;
    }

    private boolean handleModify(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /region modify <name> <pvp|build|breakBlock|explosionTNT|explosionCreeper>");
            return true;
        }

        String regionName = args[1];
        String modifyType = args[2].toLowerCase();

        ProtectedRegion targetRegion = null;
        for (ProtectedRegion region : plugin.getProtectedRegions()) {
            if (region.getName().equalsIgnoreCase(regionName)) {
                targetRegion = region;
                break;
            }
        }

        if (targetRegion == null) {
            player.sendMessage(ChatColor.RED + "Region '" + regionName + "' not found.");
            return true;
        }

        // Modification des attributs de la région
        switch (modifyType) {
            case "pvp":
                togglePVP(player, targetRegion);
                break;
            case "build":
                toggleBuild(player, targetRegion);
                break;
            case "breakblock":
                toggleBreakBlock(player, targetRegion);
                break;
            case "explosiontnt":
                toggleExplosionTNT(player, targetRegion);
                break;
            case "explosioncreeper":
                toggleExplosionCreeper(player, targetRegion);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown modify type. Use pvp, build, breakBlock, explosionTNT, or explosionCreeper.");
                return true;
        }

        plugin.saveProtectedRegions(); // Sauvegarde les modifications
        player.sendMessage(ChatColor.GREEN + "Region '" + regionName + "' modified.");
        return true;
    }

    private void togglePVP(Player player, ProtectedRegion region) {
        region.setPvpAllowed(!region.isPvpAllowed());
        player.sendMessage(ChatColor.GREEN + "PvP has been " + (region.isPvpAllowed() ? "enabled" : "disabled") + " for this region.");
    }

    private void toggleBuild(Player player, ProtectedRegion region) {
        region.setBuildAllowed(!region.isBuildAllowed());
        player.sendMessage(ChatColor.GREEN + "Building has been " + (region.isBuildAllowed() ? "enabled" : "disabled") + " for this region.");
    }

    private void toggleBreakBlock(Player player, ProtectedRegion region) {
        region.setBreakBlockAllowed(!region.isBreakBlockAllowed());
        player.sendMessage(ChatColor.GREEN + "Breaking blocks has been " + (region.isBreakBlockAllowed() ? "enabled" : "disabled") + " for this region.");
    }

    private void toggleExplosionTNT(Player player, ProtectedRegion region) {
        region.setExplosionTNTAllowed(!region.isExplosionTNTAllowed());
        player.sendMessage(ChatColor.GREEN + "TNT explosions have been " + (region.isExplosionTNTAllowed() ? "enabled" : "disabled") + " for this region.");
    }

    private void toggleExplosionCreeper(Player player, ProtectedRegion region) {
        region.setExplosionCreeperAllowed(!region.isExplosionCreeperAllowed());
        player.sendMessage(ChatColor.GREEN + "Creeper explosions have been " + (region.isExplosionCreeperAllowed() ? "enabled" : "disabled") + " for this region.");
    }

    private void startParticles(Player player, ProtectedRegion region) {
        UUID playerId = player.getUniqueId();

        stopParticles(player);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                showParticles(player, region);
            }
        }.runTaskTimer(plugin, 0L, 20L);

        particleTasks.put(playerId, task);
    }

    private void stopParticles(Player player) {
        UUID playerId = player.getUniqueId();
        BukkitTask task = particleTasks.get(playerId);

        if (task != null) {
            task.cancel();
            particleTasks.remove(playerId);
        }
    }

    private void showParticles(Player player, ProtectedRegion region) {
        Location firstPoint = region.getFirstPoint();
        Location secondPoint = region.getSecondPoint();

        if (firstPoint == null || secondPoint == null) {
            return;
        }

        World world = firstPoint.getWorld();
        if (world == null) {
            return;
        }

        double minX = Math.min(firstPoint.getX(), secondPoint.getX());
        double maxX = Math.max(firstPoint.getX(), secondPoint.getX());
        double minY = Math.min(firstPoint.getY(), secondPoint.getY());
        double maxY = Math.max(firstPoint.getY(), secondPoint.getY());
        double minZ = Math.min(firstPoint.getZ(), secondPoint.getZ());
        double maxZ = Math.max(firstPoint.getZ(), secondPoint.getZ());

        // Affiche des particules le long des bords de la région
        for (double x = minX; x <= maxX; x += 0.5) {
            player.spawnParticle(Particle.FLAME, x + 0.25, minY + 0.25, minZ + 0.25, 1, 0, 0, 0, 0); // Bord bas Z
            player.spawnParticle(Particle.FLAME, x + 0.25, minY + 0.25, maxZ + 0.75, 1, 0, 0, 0, 0); // Bord haut Z
            player.spawnParticle(Particle.FLAME, x + 0.25, maxY + 0.75, minZ + 0.25, 1, 0, 0, 0, 0); // Bord bas Z (haut Y)
            player.spawnParticle(Particle.FLAME, x + 0.25, maxY + 0.75, maxZ + 0.75, 1, 0, 0, 0, 0); // Bord haut Z (haut Y)
        }

        for (double z = minZ; z <= maxZ; z += 0.5) {
            player.spawnParticle(Particle.FLAME, minX + 0.25, minY + 0.25, z + 0.25, 1, 0, 0, 0, 0); // Bord bas X
            player.spawnParticle(Particle.FLAME, minX + 0.25, maxY + 0.75, z + 0.25, 1, 0, 0, 0, 0); // Bord bas X (haut Y)
            player.spawnParticle(Particle.FLAME, maxX + 0.75, minY + 0.25, z + 0.25, 1, 0, 0, 0, 0); // Bord haut X
            player.spawnParticle(Particle.FLAME, maxX + 0.75, maxY + 0.75, z + 0.25, 1, 0, 0, 0, 0); // Bord haut X (haut Y)
        }

        for (double y = minY; y <= maxY; y += 0.5) {
            player.spawnParticle(Particle.FLAME, minX + 0.25, y + 0.25, minZ + 0.25, 1, 0, 0, 0, 0); // Bord bas Z (bas X)
            player.spawnParticle(Particle.FLAME, minX + 0.25, y + 0.25, maxZ + 0.75, 1, 0, 0, 0, 0); // Bord haut Z (bas X)
            player.spawnParticle(Particle.FLAME, maxX + 0.75, y + 0.25, minZ + 0.25, 1, 0, 0, 0, 0); // Bord bas Z (haut X)
            player.spawnParticle(Particle.FLAME, maxX + 0.75, y + 0.25, maxZ + 0.75, 1, 0, 0, 0, 0); // Bord haut Z (haut X)
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Suggestions pour les sous-commandes
            return List.of("del", "see", "modify", "list", "info", "reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("del")) {
            return plugin.getProtectedRegions().stream()
                    .map(ProtectedRegion::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("see")) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("off");
            suggestions.addAll(plugin.getProtectedRegions().stream()
                    .map(ProtectedRegion::getName)
                    .collect(Collectors.toList()));
            return suggestions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("modify")) {
            return plugin.getProtectedRegions().stream()
                    .map(ProtectedRegion::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return plugin.getProtectedRegions().stream()
                    .map(ProtectedRegion::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("modify")) {
            return List.of("pvp", "build", "breakBlock", "explosionTNT", "explosionCreeper");
        }
        return new ArrayList<>();
    }
}