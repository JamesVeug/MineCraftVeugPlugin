package com.jamesgames.theveug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class LevelItem
{
	private final ItemStack item;
	private long id;
	private int level;
	private long xp;
	private long maxXP;
	private int defaultDamage;
	private int newDamage;
	Player holder;

	public LevelItem(ItemStack item, long id)
	{
		this.item = item;
		this.id = id;
		this.level = 1;
		this.xp = 0;
		this.maxXP = 0;
		this.defaultDamage = getDamage();
        newDamage = defaultDamage;
        
		refresh();
	}

	private void refresh()
	{
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList(3);
		lore.clear();
		lore.add(ChatColor.AQUA + "Level: " + level);
		lore.add(ChatColor.AQUA + "XP: " + xp + "/" + maxXP);
		lore.add(ChatColor.RESET + "#ID" + id);
		meta.setLore(lore);
		
		if (meta instanceof Damageable){
			((Damageable) meta).setDamage(newDamage);
        }
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

	public void setExperience(long xp, long maxXP, int level, int damage)
	{
		this.xp = xp;
		this.maxXP = maxXP;
		this.level = level;
		this.newDamage = damage;
		refresh();
	}

	public int getLevel()
	{
		return level;
	}

	public void addLevel()
	{
		level++;
	}

	public int getDefaultDamage()
	{
		return defaultDamage;
	}

	public int getDamage()
	{
		ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta instanceof Damageable){
    		return item.hasItemMeta() ? ((Damageable) item.getItemMeta()).getDamage() : 0;
        }
        
        return 0;
	}
}
