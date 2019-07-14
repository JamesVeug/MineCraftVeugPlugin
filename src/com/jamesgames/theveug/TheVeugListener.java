package com.jamesgames.theveug;

import java.util.List;
import java.util.Random;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.LevelItem.LevelItem;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;
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
	public void onBreak(BlockBreakEvent event)
	{	
		AwardXP(event);
		
		// Create new item
		CreateDrop(event);
	}

	private void CreateDrop(BlockBreakEvent event)
	{
		// Player used fists to break block?
		Player player = event.getPlayer();
		ImportedData blockData = plugin.Config.GetDataForMaterial(event.getBlock().getType());
		if (blockData == null) 
		{
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
		
		// Get list of items to drop
		List<ImportedData.ImportedDataDrop> itemDrops = blockData.ItemDrops;
		
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
		event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), dropItem);
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
		if (itemInHand == null || itemInHand.getType() == Material.AIR) 
		{
			return;
		}

		ImportedData itemData = plugin.Config.GetDataForMaterial(itemInHand.getType());
		if (itemData == null) 
		{
			return;
		}
		else if (itemData.LevelXPEquation == null || itemData.LevelXPEquation.length() == 0) 
		{
			return;
		}
		
		// Get or create LevelItem
		LevelItem item = LevelItemFactory.Instance.get(itemInHand);
		
		long xp = item.getXP() + (long)(rewardedXP * plugin.Config.getXpRate());
		long maxXP = item.getMaxXP();
		int level = item.getLevel();
		boolean leveled = xp >= maxXP; 
		while(xp >= maxXP) 
		{
			xp -= maxXP;
			level += 1;
			maxXP = plugin.Config.getMaxXPForLevel(itemInHand.getType(), level);
		}
		
		// Behavior when leveling
		if(leveled) 
		{
			String message = plugin.Config.RandomLevelUpMessage();
			message = message.replaceAll("-PLAYERNAME-", player.getDisplayName());
			message = message.replaceAll("-ITEMNAME-", getMaterialName(itemInHand.getType()));
			message = message.replaceAll("-LEVEL-", String.valueOf(level));
			Bukkit.broadcastMessage(message);
			itemInHand.setDurability((short) 0);
		}
		

		item.update(xp, maxXP, level);
	}
	
	private String getMaterialName(Material material)
	{
		String materialString = material.toString().toLowerCase();
		String[] words = materialString.split("_");
		
		materialString = StringUtils.capitalize(words[0]);
		for (int i = 1; i < words.length; i++)
		{
			materialString += " " + StringUtils.capitalize(words[i]);
		}

		return materialString;
	}

	public boolean isTool(Material material)
	{
		return materialEndsWith(material, "SPADE") || materialEndsWith(material, "PICKAXE")
				|| materialEndsWith(material, "AXE") || materialEndsWith(material, "SHOVEL");
	}

	private boolean materialEndsWith(Material material, String matchName)
	{
		return material.name().endsWith(matchName);
	}
}
