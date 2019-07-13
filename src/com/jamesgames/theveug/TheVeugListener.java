package com.jamesgames.theveug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.jamesgames.theveug.Config.ConfigData;
import com.jamesgames.theveug.Config.ImportedData;
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
		String name = event.getPlayer().getDisplayName();
		event.getPlayer().sendMessage("Hello " + name + "!");
		plugin.getLogger().info(name + " Has joined the server YAYYYYY!");
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
		ItemStack itemInHand = player.getItemInHand();
		ImportedData blockData = plugin.Config.GetDataForMaterial(event.getBlock().getType());
		if (blockData == null) 
		{
			plugin.getLogger().info(event.getBlock().getType() + " not added to TheVeugData.json earn extra drops from.");
			return;
		}
		
		int level = 1;
		if (itemInHand != null) 
		{
			LevelItem item = LevelItemFactory.Instance.get(itemInHand);
			level = item.getLevel();
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
			plugin.getLogger().info(event.getBlock().getType() + " not added to TheVeugData.json to earn XP from");
			return;
		}
		
		long rewardedXP = blockData.XPReward;
		if(rewardedXP <= 0L) 
		{
			plugin.getLogger().info(event.getBlock().getType() + " provides no XP");
			return;
		}
		
		// Player used fists to break block?
		Player player = event.getPlayer();
		ItemStack itemInHand = player.getItemInHand(); 
		if (itemInHand == null || itemInHand.getType() == Material.AIR) 
		{
			plugin.getLogger().info(player.getDisplayName() + " isn't holding anything");
			return;
		}

		ImportedData itemData = plugin.Config.GetDataForMaterial(itemInHand.getType());
		if (itemData == null) 
		{
			plugin.getLogger().info(itemInHand.getType() + " not added to TheVeugData.json earn XP from.");
			return;
		}
		if (itemData.LevelXPEquation == null || itemData.LevelXPEquation.length() == 0) 
		{
			plugin.getLogger().info(itemInHand.getType() + " has no LevelXPEquation in TheVeugData.json level up with.");
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
			String message = "%s's %s is now level %d :D";
			Bukkit.broadcastMessage(String.format(message, player.getDisplayName(), getMaterialName(itemInHand.getType()), level));
			itemInHand.setDurability((short) 0);
		}
		

		item.updateLore(xp, maxXP, level);
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
