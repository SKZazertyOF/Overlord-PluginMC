package org.skz.overlord.other;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class CustomBlockData {
    private final Location location;
    private final Material material;
    private final BlockData blockData;

    public CustomBlockData(Location location, Material material, BlockData blockData) {
        this.location = location;
        this.material = material;
        this.blockData = blockData;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return material;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}