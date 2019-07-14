package com.jamesgames.theveug;

import java.util.List;
import java.util.Random;
import javax.script.ScriptException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.LevelItem.LevelItem;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;
import com.jamesgames.theveug.LevelItem.LevelItemHandler;
import com.jamesgames.theveug.util.Util;

public class TheVeugListener implements Listener
{

	private Main plugin;

	public TheVeugListener(Main plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		player.sendMessage("Hello " + name + "!");
	}
	
	@EventHandler
    public void onKill(EntityDeathEvent e) {        
        Entity deadThing = e.getEntity();
		plugin.Log("Killed " + deadThing.getType().toString());
		if (deadThing instanceof Player)
			return;
		
        Entity killer = e.getEntity().getKiller();
        if(killer == null) {
        	return; // Died naturally
        }
        
        plugin.Log("Killer " + killer.getType().toString());
		if (!(killer instanceof Player))
			return;
		
		Player player = (Player)killer;
		ItemStack itemInHand = player.getItemInHand();
        plugin.Log("Weapon " + itemInHand.getType().toString());
        
		// Make sure this item can level up
		ImportedData weaponData = plugin.Config.GetDataForMaterial(itemInHand.getType());
		if(weaponData == null || !weaponData.canLevelUp())
		{
	        plugin.Log("No Data for weapon " + itemInHand.getType().toString());
			return;
		}

		// Make sure the killed thing has data
        plugin.Log("Got Weapon Data");
		EntityType entityType = deadThing.getType();
		ImportedData mobData = plugin.Config.GetDataForEntityType(deadThing.getType());
		if(mobData == null) 
		{
			plugin.Log("No data found for type " + deadThing.getType().toString());
			return;
		}
		else if(mobData.XPReward <= 0) 
		{
			plugin.Log(deadThing.getType().toString() + " Rewards no XP");
			return;
		}

        plugin.Log("Got Entity Data");
        
		// Add XP
		LevelItemHandler.Instance.AddExperience(itemInHand, mobData.XPReward, player);
		
		// Create new item
		CreateDrop(player, mobData, deadThing.getWorld(), deadThing.getLocation());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event)
	{	
		// Give XP
		AwardXP(event);
		
		// Create new item
		Player player = event.getPlayer();
		ImportedData blockData = plugin.Config.GetDataForMaterial(event.getBlock().getType());
		if (blockData != null) 
		{
			CreateDrop(player, blockData, event.getBlock().getWorld(), event.getBlock().getLocation());
		}
	}

	private void CreateDrop(Player player, ImportedData blockData, World world, Location location)
	{
		// Get list of items to drop
		List<ImportedData.ImportedDataDrop> itemDrops = blockData.ItemDrops;
		if(itemDrops.size() <= 0) 
		{
			// Nothing to drop
			return;
		}
		
		int level = 1;
		ItemStack itemInHand = player.getItemInHand();
		if (itemInHand != null) 
		{
			ImportedData data = plugin.Config.GetDataForMaterial(itemInHand.getType());
			if (data != null && data.LevelXPEquation != null && data.LevelXPEquation.length() > 0)
			{
				LevelItem item = LevelItemFactory.Instance.get(itemInHand);
				level = item.getLevel();
			}
		}
		
		// Choose a random item to drop
		int index = new Random().nextInt(itemDrops.size());
		ImportedData.ImportedDataDrop drop = itemDrops.get(index);
		
		// Get chance of dropping
		double chance = plugin.Config.getDropRate();
		try
		{
			double evaluated = plugin.Config.solve(drop.Equation.replace("-LEVEL-", String.valueOf(level))).doubleValue();
			chance = Util.clamp(chance * evaluated, 0, 100D);
		}
		catch (ScriptException e)
		{
			plugin.getLogger()
					.info("An exception occurred while attempting to solve item percentage for the material '"
							+ drop.material + "' : '" + drop.Equation + "'.");
			return;
		}

		// Roll random change
		double random = Math.random(); 
		if (random * 100.0D >= chance * 100.0D)
			return;

		// Drop Item
		ItemStack dropItem = new ItemStack(drop.material, 1);
		world.dropItem(location, dropItem);
	}

	private void AwardXP(BlockBreakEvent event)
	{
		ImportedData blockData = plugin.Config.GetDataForMaterial(event.getBlock().getType());
		if(blockData == null) 
		{
			return;
		}
		
		long rewardedXP = blockData.XPReward;
		if(rewardedXP <= 0L) 
		{
			return;
		}
		
		// Player used fists to break block?
		Player player = event.getPlayer();
		ItemStack itemInHand = player.getItemInHand(); 
		
		// Add XP
		LevelItemHandler.Instance.AddExperience(itemInHand, rewardedXP, player);
	}
}
