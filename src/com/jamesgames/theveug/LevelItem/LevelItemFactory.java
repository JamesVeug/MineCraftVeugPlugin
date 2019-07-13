package com.jamesgames.theveug.LevelItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jamesgames.theveug.Main;
import com.jamesgames.theveug.util.Util;

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

	public LevelItem get(ItemStack item)
	{
		long id = Util.getId(item);
		if (levelItemIdMap.containsKey(id))
		{
			// This item has already been created
			return levelItemIdMap.get(id);
		}		

		id = getNextId();
		LevelItem levelItem = new LevelItem(item, id);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : null;
		if (lore == null)
		{
			// This item has never existed so create the lore for it
			int level = 1;
			long XP = 0;
			long maxXP = plugin.Config.getMaxXPForLevel(levelItem.getItem().getType(), level);
			levelItem.updateLore(XP, maxXP, level);
		}
		else
		{
			// Use existing lore
			// NOTE: Check ID from lore does not already exist
			levelItem.useLore(lore);
			if (levelItemIdMap.containsKey(levelItem.getId())) {
				levelItem.setId(getNextId());
			}
		}
		
		levelItemIdMap.put(id, levelItem);
		return levelItem;
	}
	
	private long getNextId() {
		long id = largest++;
		while (levelItemIdMap.containsKey(id)) {
			id = largest++;
		}
		return id;
	}
}
