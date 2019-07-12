package com.jamesgames.theveug.Config;

import java.util.HashMap;

import org.bukkit.Material;

public class ConfigData
{
	public long XPReward = 0;
	public HashMap<String, String> ItemDrops = new HashMap<String, String>();
	public String LevelXPEquation = null;
	public String LevelDamageEquation = null;
	public int RandomAmountMin = 1; // Inclusive
	public int RandomAmountMax = 1; // Inclusive


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

	public ConfigData setDamageEquation(String equation)
	{
		LevelDamageEquation = equation;
		return this;
	}

	public ConfigData setXPReward(int xp)
	{
		this.XPReward = xp;
		return this;
	}
}