package com.jamesgames.theveug.Config;

import java.util.HashMap;

import org.bukkit.Material;

import com.jamesgames.theveug.util.Util;

public class ImportedData
{
	public Material material;
	public long XPReward = 0;
	public HashMap<Material, String> ItemDrops = new HashMap<Material, String>();
	public String LevelXPEquation = null;
	public String LevelDamageEquation = null;
	public int RandomAmountMin = 1; // Inclusive
	public int RandomAmountMax = 1; // Inclusive
	
	public ImportedData(Material material, ConfigData data) {
		this.material = material;
		this.XPReward = data.XPReward;
		this.LevelXPEquation = data.LevelXPEquation;
		this.LevelDamageEquation = data.LevelDamageEquation;
		this.RandomAmountMin = data.RandomAmountMin;
		this.RandomAmountMax = data.RandomAmountMax;
		for (String materialString : data.ItemDrops.keySet())
		{
			Material dropMaterial = Util.ToMaterial(materialString);
			if (dropMaterial == Material.VOID_AIR) 
			{
				System.out.println("Unknown Material type: " + materialString + ". skipping drop from " + material.toString() + ".");								
				continue;
			}
			this.ItemDrops.put(material, data.ItemDrops.get(materialString));
		}
	}
}
