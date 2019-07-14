package com.jamesgames.theveug.LevelItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.jamesgames.theveug.Main;
import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.util.Util;

public class LevelItemHandler
{
	public static LevelItemHandler Instance;
	
	private Main plugin;

	public LevelItemHandler(Main plugin)
	{
		Instance = this;
		this.plugin = plugin;
	}

	
	public void AddExperience(ItemStack itemInHand, long rewardedXP, Player holder)
	{ 
		if (itemInHand == null) 
		{
			return;
		}

		ImportedData itemData = plugin.Config.GetDataForMaterial(itemInHand.getType());
		if (itemData == null || !itemData.canLevelUp()) 
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
			message = message.replaceAll("-PLAYERNAME-", holder.getDisplayName());
			message = message.replaceAll("-ITEMNAME-", Util.getMaterialName(itemInHand.getType()));
			message = message.replaceAll("-LEVEL-", String.valueOf(level));
			Bukkit.broadcastMessage(message);
			itemInHand.setDurability((short) 0);
		}
		

		item.update(xp, maxXP, level);
	}
}
