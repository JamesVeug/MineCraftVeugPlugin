package com.jamesgames.theveug.Config;

import java.util.HashMap;

import org.bukkit.Material;

public class ConfigData
{
	public Material material;
	public long XPReward = 0;
	public HashMap<String, String> ItemDrops = new HashMap<String, String>();
	public String LevelXPEquation = null;
	public int RandomAmountMin = 1; // Inclusive
	public int RandomAmountMax = 1; // Inclusive
	
	public ConfigData(Material material) {
		this.material = material;
	}

	public ConfigData(Material material, ConfigData other) {
		this.material = material;
		this.XPReward = other.XPReward;
		this.LevelXPEquation = other.LevelXPEquation;
		this.RandomAmountMin = other.RandomAmountMin;
		this.RandomAmountMax = other.RandomAmountMax;
		for (String key: other.ItemDrops.keySet())
		{
			ItemDrops.put(key, other.ItemDrops.get(key));
		}
	}

	public ConfigData addMaterial(Material material, String equation)
	{
		ItemDrops.put(material.toString(), equation);
		return this;
	}

	public ConfigData setXPEquation(String equation)
	{
		LevelXPEquation = equation;
		return this;
	}

	public ConfigData setXPReward(int xp)
	{
		this.XPReward = xp;
		return this;
	}
}