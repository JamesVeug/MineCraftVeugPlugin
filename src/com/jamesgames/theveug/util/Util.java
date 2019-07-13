package com.jamesgames.theveug.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Util
{
	public static Material ToMaterial(String val)
	{
		val = val.toUpperCase();
		String[] vals = val.split(";");

		try
		{
			return Material.valueOf(vals[0]);
		} catch (IllegalArgumentException ex)
		{
			System.out.println("Could not find material \"" + val + "\". Skipping this value.");
		}
		return Material.VOID_AIR;
	}
	
	public static long getId(ItemStack item)
	{
		if (item != null && item.hasItemMeta())
		{
			try
			{
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList(0);
				int i = getIndexOfXPInLoreOfXP(lore) + 1;
				return Long.parseLong(ChatColor.stripColor((String) lore.get(i)).substring(3));
			} catch (IndexOutOfBoundsException | NumberFormatException localIndexOutOfBoundsException)
			{
			}
		}

		return -1L;
	}

	public static int getIndexOfXPInLoreOfXP(List<String> lore)
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

	public static double clamp(double val, double min, double max)
	{
		return Math.max(min, Math.min(max, val));
	}
}
