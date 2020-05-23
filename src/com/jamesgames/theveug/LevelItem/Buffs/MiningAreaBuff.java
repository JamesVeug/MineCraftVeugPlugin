package com.jamesgames.theveug.LevelItem.Buffs;

import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.LevelItem.LevelItem;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;
import com.jamesgames.theveug.Main;
import com.jamesgames.theveug.TheVeug;
import net.minecraft.server.v1_15_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MiningAreaBuff extends ALevelItemBuff {

	@Override
	public ALevelItemBuff CreateFromLore(String lore) {
		MiningAreaBuff buff = new MiningAreaBuff();
		String levelString = lore.substring(buff.buffName().length()).trim();
		buff.level = Integer.parseInt(levelString);
		return buff;
	}

	@Override
	public String buffName() {
		return "Mining Area";
	}

	@Override
	public String buffDescription() {
		return "Increases area of mining";
	}

	@Override
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		LevelItem levelItem = LevelItemFactory.Instance.get(itemInHand);

		int maxDistance = level;
		long totalXPEarned = 0;
		int totalProcessedBlocks = 0;
		for(int x = -maxDistance; x <= maxDistance; x++) {
			for(int y = 0; y <= maxDistance * 2; y++) {
				for(int z = -maxDistance; z <= maxDistance; z++) {
					if(x == 0 && y == 0 && z == 0) {
						continue;
					}

					// Only break blocks in a circular fashion
					double distance = distance(x, y, z);
					if(distance > maxDistance){
						continue;
					}

					// get new block position
					int newX = location.getBlockX() + x;
					int newY = location.getBlockY() + y;
					int newZ = location.getBlockZ() + z;
					Block block = player.getWorld().getBlockAt(newX, newY, newZ);

					// break block
					totalXPEarned += BreakBlockFromBuff(player, block, levelItem);
					totalProcessedBlocks++;
				}				
			}			
		}

		if(totalXPEarned > 0) {
			long xp = (long) Math.ceil(totalXPEarned * levelItem.getXPRateBuff());
			TheVeug.AwardWeaponXP(player, xp, levelItem);
		}
		System.out.println("Buff Total Awarded XP: " + totalXPEarned + " from " + totalProcessedBlocks + " blocks");
	}

	private long BreakBlockFromBuff(Player player, Block block, LevelItem levelItem) {
		Material material = block.getType();
		if(material == Material.VOID_AIR || material == Material.BEDROCK || material == Material.LAVA || material == Material.WATER || material == Material.CHEST){
			return 0;
		}

		if(!block.breakNaturally(levelItem.getItem())){
			//System.out.println("Did not break " + levelItem.getItem().toString() + " " + block.getType().toString());
			return 0;
		}

		ImportedData blockData = Main.Instance.Config.GetDataForMaterial(material);
		if (blockData != null) {

			// Create drop
			TheVeug.CreateDrop(blockData, block.getWorld(), block.getLocation(), levelItem);

			// Give XP
			//System.out.println("Added xp: " + blockData.XPReward);
			return blockData.XPReward;
		}
		else{
			//System.out.println("No Data for material: " + block.getType().toString());
		}

		return 0;
	}

	public double distance(int x, int y, int z) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public double distanceTo(Location p, int x, int y, int z) {
		return Math.sqrt(Math.pow(x - p.getBlockX(), 2) + Math.pow(y - p.getBlockY(), 2) + Math.pow(z - p.getBlockZ(), 2));
	}

	@Override
	public String loreDescription() {
		return buffName() + " " + level;
	}
}
