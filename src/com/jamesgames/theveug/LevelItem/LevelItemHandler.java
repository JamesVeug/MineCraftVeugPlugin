package com.jamesgames.theveug.LevelItem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

	
	public void AddExperience(LevelItem item, long rewardedXP, Player holder)
	{ 
		if (item == null)
		{
			return;
		}

		double xpRate = plugin.Config.getXpRate();

		long xp = item.getXP() + (long)(rewardedXP * xpRate);
		long maxXP = item.getMaxXP();
		int level = item.getLevel();
		boolean leveled = xp >= maxXP; 
		while(xp >= maxXP)
		{
			xp -= maxXP;
			level += 1;
			maxXP = plugin.Config.getMaxXPForLevel(item.getItem().getType(), level);

			if(maxXP <= 0){
				break;
			}
		}
		
		// Behavior when leveling
		if(leveled) 
		{
			String message = plugin.Config.RandomLevelUpMessage();
			message = message.replaceAll("-PLAYERNAME-", holder.getDisplayName());
			message = message.replaceAll("-ITEMNAME-", Util.getMaterialName(item.getItem().getType()));
			message = message.replaceAll("-LEVEL-", String.valueOf(level));
			Bukkit.broadcastMessage(message);
			item.getItem().setDurability((short) 0);
		}
		

		item.update(xp, maxXP, level);
		item.refreshBuffs();
	}
}
