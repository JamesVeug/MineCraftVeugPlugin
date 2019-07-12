package com.jamesgames.theveug;

import java.util.HashMap;

import javax.script.ScriptException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.jamesgames.theveug.Config.ConfigData;
import com.jamesgames.theveug.Config.ImportedData;

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
		int level = 1;
		if (itemInHand != null) 
		{
			LevelItem item = LevelItemFactory.Instance.getLevelItem(itemInHand);
			level = item.getLevel();
		}
		
		ImportedData data = plugin.Config.GetDataForMaterial(itemInHand.getType());
		HashMap<Material, String> itemDrops = data.ItemDrops;
		for (Material material : itemDrops.keySet())
		{
			String equation = itemDrops.get(material);
			
			double chance = 0.0D;
			try 
			{
				chance = plugin.Config.solve(equation.replace("-LEVEL-", String.valueOf(level))).doubleValue();
			} catch (ScriptException e) 
			{
				plugin.getLogger().info("An exception occurred while attempting to solve item percentage for the material '" + material + "' : '" + equation + "'.");
		     }
			
			if (Math.random() * 100.0D > chance * 100.0D)
				continue;
				
			ItemStack dropItem = new ItemStack(material, 1);
			event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), dropItem);
		}
	}

	private void AwardXP(BlockBreakEvent event)
	{
		long rewardedXP = plugin.Config.GetDataForMaterial(event.getBlock().getType()).XPReward;
		if(rewardedXP <= 0L) 
		{

			plugin.getLogger().info(event.getBlock().getType() + " Provides no XP");
			return;
		}
		
		// Player used fists to break block?
		Player player = event.getPlayer();
		ItemStack itemInHand = player.getItemInHand(); 
		if (itemInHand == null) 
		{
			plugin.getLogger().info(player.getDisplayName() + " isn't holding anything");
			return;
		}
				
		// Didn't use a tool to break the block
		if (!isTool(itemInHand.getType())) 
		{
			plugin.getLogger().info(itemInHand.getType() + " isn't a tool");
			return;
		}
		
		// Get or create LevelItem
		LevelItem item = LevelItemFactory.Instance.getLevelItem(itemInHand);
		
		long xp = item.getXP() + rewardedXP;
		long maxXP = item.getMaxXP();
		int level = item.getLevel();
		
		while(xp >= maxXP) 
		{
			xp -= maxXP;
			level += 1;
			maxXP = plugin.Config.getMaxXPForLevel(itemInHand.getType(), level);
		}

		plugin.getLogger().info(String.format("Item State: Levle: {0} XP: {1}/{2}", level, xp, maxXP));
		int damage = plugin.Config.getDamageForLevel(item.getItem().getType(), level, item.getDamage(), item.getDefaultDamage());
		item.setExperience(xp, maxXP, level, damage);
	}

	public boolean isTool(Material material)
	{
		return materialEndsWith(material, "SPADE") || materialEndsWith(material, "PICKAXE")
				|| materialEndsWith(material, "AXE");
	}

	private boolean materialEndsWith(Material material, String matchName)
	{
		return material.name().endsWith(matchName);
	}
}
