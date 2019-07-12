package com.jamesgames.theveug.util;

import org.bukkit.Material;

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

}
