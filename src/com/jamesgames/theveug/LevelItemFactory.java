package com.jamesgames.theveug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LevelItemFactory
{
	public static LevelItemFactory Instance;
	
	private long largest = 2;

	public final HashMap<Long, LevelItem> levelItemIdMap = new HashMap<Long, LevelItem>();

	private Main plugin;
	
	public LevelItemFactory(Main plugin) 
	{
		Instance = this;
		this.plugin = plugin;
	}

	public LevelItem Create(ItemStack item)
	{
		long id = largest++;
		return Create(item, id);
	}

	public LevelItem Create(ItemStack item, long id)
	{
		LevelItem levelItem = new LevelItem(item, id);
		long maxXP = plugin.Config.getExperienceForLevel(levelItem.getLevel());
		int damage = plugin.Config.getDamageForLevel(levelItem.getItem().getType(), levelItem.getLevel(), levelItem.getDamage(), levelItem.getDefaultDamage());
		levelItem.setExperience(0, maxXP, levelItem.getLevel(), damage);
		
		levelItemIdMap.put(id, levelItem);
		return levelItem;
	}

	public LevelItem getLevelItem(ItemStack item)
	{
		long id = getId(item);
		if (id < 0L)
			return Create(item);

		LevelItem levelItem = getLevelItem(id);
		if (levelItem == null)
			levelItem = Create(item, id);

		return levelItem;
	}

	private LevelItem getLevelItem(long id)
	{
		return (LevelItem) levelItemIdMap.get(Long.valueOf(id));
	}

	public long getId(ItemStack item)
	{
		if (item != null && item.hasItemMeta())
		{
			try
			{
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList(0);
				int i = get(lore) + 1;
				return Long.parseLong(ChatColor.stripColor((String) lore.get(i)).substring(3));
			} catch (IndexOutOfBoundsException | NumberFormatException localIndexOutOfBoundsException)
			{
			}
		}

		return -1L;
	}

	private int get(List<String> lore)
	{
		for (int i = 0; i < lore.size(); i++)
		{
			if (ChatColor.stripColor((String) lore.get(i)).startsWith("XP: "))
			{
				return i;
			}
		}
		return lore.size() - 1;
	}
}
