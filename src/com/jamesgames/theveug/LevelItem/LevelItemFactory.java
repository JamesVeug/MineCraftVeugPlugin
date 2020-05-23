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

	private long nextID = 1;

	public final HashMap<Long, LevelItem> levelItemIdMap = new HashMap<Long, LevelItem>();

	private Main plugin;

	public LevelItemFactory(Main plugin)
	{
		Instance = this;
		this.plugin = plugin;
	}

	public LevelItem get(ItemStack item)
	{
		if(item == null){
			return null;
		}

		long id = Util.getId(item);
		plugin.Log("Getting Item: " + id);
		if (id <= 0)
		{
			// New Item
			id = getNextId();
		}
		else if (levelItemIdMap.containsKey(id))
		{
			// This item has already been created
			LevelItem levelItem = levelItemIdMap.get(id);
			if (levelItem.getItem().equals(item))
			{
				plugin.Log("Same Item: " + id);
				return levelItem;
			}

			long newID = getNextId();
			System.err.println("Avoided conflict getting Item: " + id + " and now creating a new ID: " + newID);
			id = newID;
		}

		LevelItem levelItem = new LevelItem(item, id);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : null;
		if (lore == null)
		{
			// This item has never existed so create the lore for it
			int level = 1;
			long XP = 0;
			long maxXP = plugin.Config.getMaxXPForLevel(levelItem.getItem().getType(), level);
			levelItem.update(XP, maxXP, level);
			plugin.Log("Assigning new Lore: " + id);
		}
		else
		{
			// Use existing lore
			// NOTE: Check ID from lore does not already exist
			levelItem.useLore(lore, false);
			plugin.Log("Using old lore new Lore: " + id);
		}

		levelItemIdMap.put(levelItem.getId(), levelItem);
		return levelItem;
	}

	private long getNextId()
	{
		long id = nextID++;
		while (levelItemIdMap.containsKey(id))
		{
			id = nextID++;
		}
		return id;
	}
}
