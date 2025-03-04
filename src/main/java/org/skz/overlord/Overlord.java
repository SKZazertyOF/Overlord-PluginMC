package org.skz.overlord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.skz.overlord.commands.*;
import org.skz.overlord.listener.MainListener;
import org.skz.overlord.other.ProtectedRegion;

import java.util.ArrayList;
import java.util.List;

public final class Overlord extends JavaPlugin {

    private Location firstPoint;
    private Location secondPoint;
    private final List<ProtectedRegion> protectedRegions = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadProtectedRegions();


        registerCommands();
        registerListeners();
        initializeScoreboard();

        logInfo("Command ResetDeathScoreBoard initialized");
        logInfo(ChatColor.DARK_AQUA + "Plugin: " + ChatColor.DARK_PURPLE + "[OVERLORD]" + ChatColor.DARK_GREEN + " has been enabled");
    }

    @Override
    public void onDisable() {
        saveProtectedRegions();
        saveConfig();
        logInfo(ChatColor.DARK_AQUA + "Plugin: " + ChatColor.DARK_PURPLE + "[OVERLORD]" + ChatColor.DARK_RED + " has been disabled!");
    }

    private void registerCommands() {
        getCommand("resetdeaths").setExecutor(new ResetDeathScoreBoard());
        getCommand("createnewworld").setExecutor(new CreateNewWorld());
        getCommand("wando").setExecutor(new GiveWandProtect());
        getCommand("protect").setExecutor(new ProtectRegion(this));
        getCommand("region").setExecutor(new RegionCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new MainListener(this), this);
    }

    private void logInfo(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[Info] " + ChatColor.WHITE + message);
    }

    private void initializeScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getMainScoreboard();
        if (scoreboard.getObjective("Deaths") == null) {
            Objective objective = scoreboard.registerNewObjective("Deaths", "deathCount", "Deaths");
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
    }

    public ProtectedRegion getProtectedRegionAt(Location location) {
        for (ProtectedRegion region : protectedRegions) {
            if (region.isInRegion(location)) {
                return region;
            }
        }
        return null;
    }

    public void saveProtectedRegions() {
        FileConfiguration config = getConfig();

        config.set("protectedRegions", null);

        for (int i = 0; i < protectedRegions.size(); i++) {
            ProtectedRegion region = protectedRegions.get(i);
            config.set("protectedRegions." + i + ".name", region.getName());
            config.set("protectedRegions." + i + ".firstPoint", region.getFirstPoint());
            config.set("protectedRegions." + i + ".secondPoint", region.getSecondPoint());
            config.set("protectedRegions." + i + ".pvpAllowed", region.isPvpAllowed());
            config.set("protectedRegions." + i + ".buildAllowed", region.isBuildAllowed());
            config.set("protectedRegions." + i + ".breakBlockAllowed", region.isBreakBlockAllowed());
            config.set("protectedRegions." + i + ".explosionTNTAllowed", region.isExplosionTNTAllowed());
            config.set("protectedRegions." + i + ".explosionCreeperAllowed", region.isExplosionCreeperAllowed());
        }

        saveConfig();
    }

    private void loadProtectedRegions() {
        FileConfiguration config = getConfig();

        if (config.contains("protectedRegions")) {
            for (String key : config.getConfigurationSection("protectedRegions").getKeys(false)) {
                String name = config.getString("protectedRegions." + key + ".name");
                Location firstPoint = config.getLocation("protectedRegions." + key + ".firstPoint");
                Location secondPoint = config.getLocation("protectedRegions." + key + ".secondPoint");

                if (name != null && firstPoint != null && secondPoint != null) {
                    ProtectedRegion region = new ProtectedRegion(name, firstPoint, secondPoint);
                    region.setPvpAllowed(config.getBoolean("protectedRegions." + key + ".pvpAllowed", false));
                    region.setBuildAllowed(config.getBoolean("protectedRegions." + key + ".buildAllowed", false));
                    region.setBreakBlockAllowed(config.getBoolean("protectedRegions." + key + ".breakBlockAllowed", false));
                    region.setExplosionTNTAllowed(config.getBoolean("protectedRegions." + key + ".explosionTNTAllowed", false));
                    region.setExplosionCreeperAllowed(config.getBoolean("protectedRegions." + key + ".explosionCreeperAllowed", false));
                    protectedRegions.add(region);
                }
            }
            logInfo("Protected regions loaded from config.yml");
        }
    }

    public Location getFirstPoint() {
        return firstPoint;
    }

    public void setFirstPoint(Location firstPoint) {
        this.firstPoint = firstPoint;
    }

    public Location getSecondPoint() {
        return secondPoint;
    }

    public void setSecondPoint(Location secondPoint) {
        this.secondPoint = secondPoint;
    }

    public void addProtectedRegion(ProtectedRegion region) {
        protectedRegions.add(region);
        saveProtectedRegions(); // Save regions to config.yml
    }

    public List<ProtectedRegion> getProtectedRegions() {
        return protectedRegions;
    }

    public void reloadProtectedRegions() {
        // Implémente la logique pour recharger les régions depuis le fichier de configuration
        // Par exemple, si tu utilises un fichier YAML :
        // protectedRegions = loadFromConfig();
    }

    public boolean isInAnyProtectedRegion(Location location) {
        for (ProtectedRegion region : protectedRegions) {
            if (region.isInRegion(location)) {
                return true;
            }
        }
        return false;
    }
}