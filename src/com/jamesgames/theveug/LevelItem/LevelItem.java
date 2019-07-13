package com.jamesgames.theveug.LevelItem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LevelItem
{
	private final ItemStack item;
	private long id;
	private int level;
	private long xp;
	private long maxXP;

	public LevelItem(ItemStack item, long id)
	{
		this.item = item;
		this.id = id;
		this.level = 1;
		this.xp = 0;
		this.maxXP = 0;
	}

	public void refresh()
	{
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList(3);
		lore.clear();
		lore.add(ChatColor.AQUA + "Level: " + this.level);
		lore.add(ChatColor.AQUA + "XP: " + this.xp + "/" + this.maxXP);
		lore.add(ChatColor.RESET + "#ID" + this.id);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public ItemStack getItem()
	{
		return item;
	}

	public long getId()
	{
		return id;
	}

	public long getXP()
	{
		return xp;
	}

	public long getMaxXP()
	{
		return maxXP;
	}

	public void updateLore(long xp, long maxXP, int level)
	{
		this.xp = xp;
		this.maxXP = maxXP;
		this.level = level;
		refresh();
	}
	
	public void useLore(List<String> lore) 
	{
		// Update from lore
		this.level = Integer.parseInt(ChatColor.stripColor((String) lore.get(0)).substring(7));
		this.id = Integer.parseInt(ChatColor.stripColor((String) lore.get(2)).substring(3));
		
		String xpString = ChatColor.stripColor((String) lore.get(1)).substring(4);
		int length = xpString.indexOf("/");
		this.xp = TryParse(xpString.substring(0, length), 0L);
		this.maxXP = TryParse(xpString.substring(length + 1), 1L);
		
		// Update lore
		refresh();
	}
	
	private Long TryParse(String s, Long defaultValue)
	{
		try
		{
			return Long.parseLong(s);
		}
		catch (NumberFormatException exception)
		{
			System.err.println(exception);
			return defaultValue;
		}
	}

	public int getLevel()
	{
		return level;
	}

	public void setId(long id)
	{
		this.id = id;
	}
}
