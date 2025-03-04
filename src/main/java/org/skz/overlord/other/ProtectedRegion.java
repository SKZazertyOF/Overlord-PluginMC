package org.skz.overlord.other;

import org.bukkit.Location;

public class ProtectedRegion {
    private final String name;
    private final Location firstPoint;
    private final Location secondPoint;
    private boolean pvpAllowed = false; // PVP désactivé par défaut
    private boolean buildAllowed = false; // Construction désactivée par défaut
    private boolean breakBlockAllowed = false; // Destruction de blocs désactivée par défaut
    private boolean explosionTNTAllowed = false; // Explosions de TNT désactivées par défaut
    private boolean explosionCreeperAllowed = false; // Explosions de Creeper désactivées par défaut


    public ProtectedRegion(String name, Location firstPoint, Location secondPoint) {
        this.name = name;
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    public String getName() {
        return name;
    }

    public Location getFirstPoint() {
        return firstPoint;
    }

    public Location getSecondPoint() {
        return secondPoint;
    }

    public boolean isPvpAllowed() {
        return pvpAllowed;
    }

    public void setPvpAllowed(boolean pvpAllowed) {
        this.pvpAllowed = pvpAllowed;
    }

    public boolean isBuildAllowed() {
        return buildAllowed;
    }

    public void setBuildAllowed(boolean buildAllowed) {
        this.buildAllowed = buildAllowed;
    }

    public boolean isBreakBlockAllowed() {
        return breakBlockAllowed;
    }

    public void setBreakBlockAllowed(boolean breakBlockAllowed) {
        this.breakBlockAllowed = breakBlockAllowed;
    }

    public boolean isExplosionTNTAllowed() {
        return explosionTNTAllowed;
    }

    public void setExplosionTNTAllowed(boolean explosionTNTAllowed) {
        this.explosionTNTAllowed = explosionTNTAllowed;
    }

    public boolean isExplosionCreeperAllowed() {
        return explosionCreeperAllowed;
    }

    public void setExplosionCreeperAllowed(boolean explosionCreeperAllowed) {
        this.explosionCreeperAllowed = explosionCreeperAllowed;
    }

    public boolean isInRegion(Location location) {
        double minX = Math.min(firstPoint.getX(), secondPoint.getX());
        double maxX = Math.max(firstPoint.getX(), secondPoint.getX());
        double minY = Math.min(firstPoint.getY(), secondPoint.getY());
        double maxY = Math.max(firstPoint.getY(), secondPoint.getY());
        double minZ = Math.min(firstPoint.getZ(), secondPoint.getZ());
        double maxZ = Math.max(firstPoint.getZ(), secondPoint.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
                location.getY() >= minY && location.getY() <= maxY &&
                location.getZ() >= minZ && location.getZ() <= maxZ;
    }
}