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
import org.bukkit.util.Vector;

public class TunnelMiningBuff extends ALevelItemBuff {

	@Override
	public ALevelItemBuff CreateFromLore(String lore) {
		TunnelMiningBuff buff = new TunnelMiningBuff();
		if(lore != null && !lore.isEmpty()) {
			String levelString = lore.substring(buff.buffName().length()).trim();
			buff.level = Integer.parseInt(levelString);
		}
		return buff;
	}

	@Override
	public String buffName() {
		return "Tunnel Mining";
	}

	@Override
	public String buffDescription() {
		return "Mines a straight line from where the player is standing";
	}

	@Override
	public void onBreak(BlockBreakEvent event) {
		System.out.println("Starting Tunnel Mining Buff!");
		Player player = event.getPlayer();
		Block block = event.getBlock();
		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		LevelItem levelItem = LevelItemFactory.Instance.get(itemInHand);

		int maxDistance = level + 1;
		int height = 5;
		int width = 5;
		long totalXPEarned = 0;


		Vector blockPosition = block.getLocation().toVector();
		Vector playerPosition = player.getLocation().toVector();
		playerPosition.setX(playerPosition.getBlockX());
		playerPosition.setY(playerPosition.getBlockY() + 1);
		playerPosition.setZ(playerPosition.getBlockZ());

		//System.out.println("Tunnel Mining: Block " + blockPosition);
		//System.out.println("Tunnel Mining: Player " + playerPosition);

		// Get max direction we want to move in
		Vector zDirection = new Vector().copy(player.getLocation().getDirection()).normalize();
		Vector xDirection = new Vector().copy(zDirection).rotateAroundY(90).normalize();
		Vector yDirection = new Vector().copy(zDirection).rotateAroundZ(90).normalize();

		Vector startPosition = new Vector().copy(blockPosition);
		Vector widthVector = new Vector();
		Vector heightVector = new Vector();
		Vector depthVector = new Vector();
		Vector deltaVector = new Vector();
		for(int x = -width / 2; x <= width / 2; x++){
			for(int y = -height / 2; y <= height / 2; y++){
				for(int z = 0; z < maxDistance; z++){

					// Calculate directions
					widthVector.copy(xDirection).multiply(x);
					heightVector.copy(yDirection).multiply(y);
					depthVector.copy(zDirection).multiply(z);

					// Calculate final block position
					deltaVector.zero();
					deltaVector.add(startPosition).add(widthVector).add(heightVector).add(depthVector);

					int xPos = (int)Math.round(deltaVector.getX());
					int yPos = (int)Math.round(deltaVector.getY());
					int zPos = (int)Math.round(deltaVector.getZ());
					Block subBlock = player.getWorld().getBlockAt(xPos, yPos, zPos);

					//System.out.println("Tunnel Mining: POS: " + x + ", " + y + ", " + z);
					/*System.out.println("Tunnel Mining: POS: " + x + ", " + y + ", " + z);
					System.out.println("Tunnel Mining: SIZ: " + width + ", " + height + ", " + maxDistance);
					System.out.println("Tunnel Mining: Step " + xPos + ", " + yPos + ", " + zPos);*/

					// break block
					totalXPEarned += BreakBlockFromBuff(player, subBlock, levelItem);
				}
			}
		}

		if(totalXPEarned > 0) {
			long xp = (long) Math.ceil(totalXPEarned * levelItem.getXPRateBuff());
			TheVeug.AwardWeaponXP(player, xp, levelItem);
		}
		System.out.println("Ending Tunnel Mining Buff!");
	}

	private long BreakBlockFromBuff(Player player, Block block, LevelItem levelItem) {
		Material material = block.getType();
		if(material == Material.VOID_AIR || material == Material.BEDROCK ||
				material == Material.LAVA || material == Material.WATER ||
				material == Material.CHEST || material == Material.TORCH){
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

	@Override
	public int maxLevel() {
		return 5;
	}
}
