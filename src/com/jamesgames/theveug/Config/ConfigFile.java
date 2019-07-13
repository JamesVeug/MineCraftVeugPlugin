package com.jamesgames.theveug.Config;

import java.util.HashMap;

import org.bukkit.Material;

public class ConfigFile
{
	public double XPRate;
	public double DropRate;
	public HashMap<String, ConfigData> MaterialData = new HashMap<String, ConfigData>();
	
	public void AddMaterialData(Material material) {
		
	}
}
