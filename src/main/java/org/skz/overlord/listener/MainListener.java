package org.skz.overlord.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.skz.overlord.Overlord;
import org.skz.overlord.other.ProtectedRegion;

import java.util.*;

public class MainListener implements Listener {

    private final Overlord plugin;
    private final Set<String> alreadyCounted = new HashSet<>();

    private Location firstPoint = null;
    private Location secondPoint = null;

    public MainListener(Overlord plugin) {
        this.plugin = plugin;
    }

    private static final Map<String, ChatColor> WORLD_TITLES = Map.of(
            "world_nether", ChatColor.DARK_RED,
            "world_the_end", ChatColor.YELLOW,
            "world", ChatColor.DARK_GREEN
    );

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player playerJoined = event.getPlayer();
        event.setJoinMessage(null);

        String message = ChatColor.DARK_GREEN + playerJoined.getName() + " joined the server!";
        Bukkit.broadcastMessage(message);

        playerJoined.playSound(playerJoined.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        System.out.println(playerJoined.getName() + " joined the server");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player playerQuit = event.getPlayer();
        event.setQuitMessage(null);

        String message = ChatColor.DARK_RED + playerQuit.getName() + " left the server!";
        Bukkit.broadcastMessage(message);

        System.out.println(playerQuit.getName() + " left the server");
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World toWorld = player.getWorld();
        String worldName = toWorld.getName();

        ChatColor color = WORLD_TITLES.getOrDefault(worldName, ChatColor.DARK_PURPLE);
        String title = switch (worldName) {
            case "world_nether" -> "[Nether]";
            case "world_the_end" -> "[End]";
            case "world" -> "[Overworld]";
            default -> "[Unknown]";
        };

        player.sendTitle(color + title, "", 10, 40, 10);
    }

    @EventHandler
    public void onSpawnEntity(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        int effectChoice = new Random().nextInt(9);

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            applyRandomEffect(livingEntity, effectChoice);
        }
    }

    private void applyRandomEffect(LivingEntity livingEntity, int effectChoice) {
        switch (effectChoice) {
            case 0:
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false));
                break;

            case 1:
                livingEntity.setMaxHealth(livingEntity.getMaxHealth() + 10);
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1, 10, true));
                break;

            case 2:
                livingEntity.setSilent(true);
                break;

            case 3:
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1, false));
                break;

            case 4:
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1, false));
                break;

            case 5:
                if (livingEntity instanceof Zombie) {
                    equipRandomArmor(livingEntity);
                }
                break;

            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.STONE_AXE && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(ChatColor.RED + "Protection Axe")) {

                event.setCancelled(true);

                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock == null) {
                    player.sendMessage(ChatColor.RED + "No block in sight!");
                    return;
                }

                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    plugin.setFirstPoint(targetBlock.getLocation());
                    player.sendMessage(ChatColor.GREEN + "First point set at: " + formatLocation(targetBlock.getLocation()));
                } else if (event.getAction().toString().contains("LEFT_CLICK")) {
                    plugin.setSecondPoint(targetBlock.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Second point set at: " + formatLocation(targetBlock.getLocation()));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location blockLocation = event.getBlock().getLocation();

        ProtectedRegion region = plugin.getProtectedRegionAt(blockLocation);
        if (region != null && !region.isBreakBlockAllowed() && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break blocks in this protected region.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location blockLocation = event.getBlock().getLocation();

        if (plugin.isInAnyProtectedRegion(blockLocation)) {
            ProtectedRegion region = plugin.getProtectedRegionAt(blockLocation);
            if (region != null && !region.isBuildAllowed() && !player.isOp()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place blocks in this protected region.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Location location = player.getLocation();

            if (plugin.isInAnyProtectedRegion(location)) {
                ProtectedRegion region = plugin.getProtectedRegionAt(location);
                if (region != null && !region.isPvpAllowed()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "PVP is disabled in this protected region.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Location location = event.getBlock().getLocation();

        if (plugin.isInAnyProtectedRegion(location)) {
            ProtectedRegion region = plugin.getProtectedRegionAt(location);
            if (region != null && !region.isExplosionTNTAllowed()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();

        if (plugin.isInAnyProtectedRegion(location)) {
            ProtectedRegion region = plugin.getProtectedRegionAt(location);
            if (region != null && !region.isExplosionCreeperAllowed()) {
                event.setCancelled(true);
            }
        }
    }

    public void equipRandomArmor(LivingEntity entity) {
        ItemStack helmet = getRandomArmorPiece(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
                Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET);
        ItemStack chestplate = getRandomArmorPiece(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
        ItemStack leggings = getRandomArmorPiece(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
                Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS);
        ItemStack boots = getRandomArmorPiece(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
                Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS);

        entity.getEquipment().setHelmet(helmet != null ? helmet : new ItemStack(Material.AIR));
        entity.getEquipment().setChestplate(chestplate != null ? chestplate : new ItemStack(Material.AIR));
        entity.getEquipment().setLeggings(leggings != null ? leggings : new ItemStack(Material.AIR));
        entity.getEquipment().setBoots(boots != null ? boots : new ItemStack(Material.AIR));
    }

    public ItemStack getRandomArmorPiece(Material... materials) {
        Random random = new Random();

        if (random.nextInt(100) < 30) {
            return null;
        }

        Material randomArmorMaterial = materials[random.nextInt(materials.length)];
        return new ItemStack(randomArmorMaterial);
    }

    private String formatLocation(Location location) {
        return String.format("X: %d, Y: %d, Z: %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}