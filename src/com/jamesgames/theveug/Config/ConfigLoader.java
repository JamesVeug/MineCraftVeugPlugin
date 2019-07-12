package com.jamesgames.theveug.Config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.jamesgames.theveug.Main;

public class ConfigLoader
{	
	public ConfigFile getJsonConfig()
	{
		String path = Bukkit.getWorldContainer().getAbsolutePath();
		path = path.substring(0, path.length() - 1) + "/plugins/TheVeug/";
		File jsonConfig = new File(path, "TheVeugData.json");
		System.out.println("Loading Config from: " + jsonConfig.getAbsolutePath());

		try
		{
			// Create the config if it doesn't already exist.
			Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
			if (!jsonConfig.exists() && jsonConfig.createNewFile())
			{
				// Get a default map of blocks. You could just use a blank map, however.
				ConfigFile defaultMap = getDefaults();

				// Convert the map to JSON format. There is a built in (de)serializer for it
				// already.
				String json = gson.toJson(defaultMap, new TypeToken<ConfigFile>() {}.getType());
				FileWriter writer = new FileWriter(jsonConfig);
				// Write to the file you passed
				writer.write(json);
				// Always close when done.
				writer.close();
			}

			// If the file exists (or we just made one exist), convert it from JSON format
			// to a populated Map object
			return gson.fromJson(new FileReader(jsonConfig), new TypeToken<ConfigFile>() {
			}.getType());
		} catch (IOException e)
		{
			// Print an error if something fails. Please use a real logger, not System.out.
			System.out.println("Error creating default configuration.");
		}

		return null;
	}

	private static ConfigFile getDefaults()
	{
		ConfigFile file = new ConfigFile();

		// XPEquation
		file.XPRate = 1.0f;
		file.DropRate = 1.0f;

		// Item / Block Data
		file.MaterialData = new HashMap<String, ConfigData>();
		
		String defaultLevelEquation = "-LEVEL- * 0.01";
		String defaultDamageEquation = "-DEFAULT_DAMAGE- * -LEVEL-";
		for (Material material : Material.values())
		{
			String m = material.toString().toLowerCase();

			// weapons
			if (m.endsWith("sword"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setDamageEquation(defaultDamageEquation));
			}

			// tools
			if (m.endsWith("pickaxe"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setDamageEquation(defaultDamageEquation));
			}
			if (m.endsWith("shovel"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setDamageEquation(defaultDamageEquation));
			}
			if (m.endsWith("axe"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setDamageEquation(defaultDamageEquation));
			}
			if (m.endsWith("hoe"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setDamageEquation(defaultDamageEquation));
			}

			// ingots
			if (m.endsWith("ingot"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}

			// wool
			if (m.endsWith("wool"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}

			// ores
			if (m.endsWith("ore"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPReward(1));
			}

			// blocks of ore?
			if (m.endsWith("block"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPReward(1));
			}

			// armor
			if (m.endsWith("boots"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}
			if (m.endsWith("helmet"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}
			if (m.endsWith("leggings"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}
			if (m.endsWith("chestplate"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}

			if (m.endsWith("horse_armor"))
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPEquation(defaultLevelEquation));
			}

			// default block types
			if (material == Material.STONE || material == Material.DIRT || material == Material.SAND
					|| material == Material.GRAVEL || material == material.ANDESITE)
			{
				file.MaterialData.put(material.toString(), new ConfigData().setXPReward(1));
			}
		}

		return file;
	}
}
