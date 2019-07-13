package com.jamesgames.theveug.Config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
		// Build map
		ArrayList<ConfigData> Everything = new ArrayList<ConfigData>();
		ArrayList<ConfigData> MaterialData = new ArrayList<ConfigData>();
		ArrayList<ConfigData> tools = new ArrayList<ConfigData>();
		ArrayList<ConfigData> armour = new ArrayList<ConfigData>();
		ArrayList<ConfigData> ores = new ArrayList<ConfigData>();
		ArrayList<ConfigData> mobDrops = new ArrayList<ConfigData>();
		
		String defaultLevelEquation = "-LEVEL- * 50";
		String defaultDamageEquation = "-DEFAULT_DAMAGE- * -LEVEL-";
		ConfigData blockTemplate = new ConfigData(Material.VOID_AIR).setXPReward(1);
		ConfigData weaponTemplate = new ConfigData(Material.VOID_AIR).setXPEquation(defaultLevelEquation);
		
		for (Material material : Material.values())
		{
			String m = material.toString().toLowerCase();
			ConfigData configData;
			
			// weapons
			if (m.endsWith("sword"))
			{
				configData = new ConfigData(material, weaponTemplate);
				tools.add(configData);
				Everything.add(configData);
			}

			// tools
			if (m.endsWith("pickaxe"))
			{
				configData = new ConfigData(material, weaponTemplate);
				tools.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("shovel"))
			{
				configData = new ConfigData(material, weaponTemplate);
				tools.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("axe"))
			{
				configData = new ConfigData(material, weaponTemplate);
				tools.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("hoe"))
			{
				configData = new ConfigData(material, weaponTemplate);
				tools.add(configData);
				Everything.add(configData);
			}

			// ingots
			if (m.endsWith("ingot"))
			{
				configData = new ConfigData(material, new ConfigData(material, blockTemplate));
				ores.add(configData);
				Everything.add(configData);
			}

			// mob drops
			if (m.endsWith("wool"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}
			if (m.contains("spider"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}
			if (m.contains("arrow"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}
			if (m.contains("leather"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("powder"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("egg"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("flesh"))
			{
				configData = new ConfigData(material, blockTemplate);
				mobDrops.add(configData);
				Everything.add(configData);
			}

			// ores
			if (m.endsWith("ore"))
			{
				configData = new ConfigData(material, new ConfigData(material, blockTemplate));
				ores.add(configData);
				Everything.add(configData);
			}

			// blocks of ore?
			if (m.endsWith("block"))
			{
				configData = new ConfigData(material, new ConfigData(material, blockTemplate));
				ores.add(configData);
				Everything.add(configData);
			}

			// log
			if (m.endsWith("log"))
			{
				configData = new ConfigData(material, blockTemplate);
				MaterialData.add(configData);
				Everything.add(configData);
			}

			// planks
			if (m.endsWith("planks"))
			{
				configData = new ConfigData(material, blockTemplate);
				MaterialData.add(configData);
				Everything.add(configData);
			}

			// armor
			if (m.endsWith("boots"))
			{
				configData = new ConfigData(material, new ConfigData(material));
				armour.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("helmet"))
			{
				configData = new ConfigData(material, new ConfigData(material));
				armour.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("leggings"))
			{
				configData = new ConfigData(material, new ConfigData(material));
				armour.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("chestplate"))
			{
				configData = new ConfigData(material, new ConfigData(material));
				armour.add(configData);
				Everything.add(configData);
			}
			if (m.endsWith("horse_armor"))
			{
				configData = new ConfigData(material, new ConfigData(material));
				armour.add(configData);
				Everything.add(configData);
			}

			// default block types
			if (material == Material.STONE || material == Material.DIRT || material == Material.GRASS_BLOCK
					|| material == Material.GRAVEL || material == Material.ANDESITE || material == Material.DIORITE
					|| material == Material.SAND || material == Material.SANDSTONE)
			{
				configData = new ConfigData(material, blockTemplate);
				MaterialData.add(configData);
				Everything.add(configData);
			}
			

			if (m.contains("bucket") || m.contains("wheat") || m.contains("flower") || 
					m.contains("stick") || m.contains("nether") || m.contains("slime") ||
					m.contains("melon") || m.contains("pumpkin") || m.contains("bean") || 
					m.contains("potion"))
			{
				configData = new ConfigData(material, new ConfigData(material));
				Everything.add(configData);
			}
		}

		// Create file
		ConfigFile file = new ConfigFile();

		// XPEquation
		file.XPRate = 1.0f;
		file.DropRate = 1.0f;

		// Apply random drops to random items
		while(Everything.size() > 0) 
		{
			int index = new Random().nextInt(Everything.size());
			ConfigData config = Everything.remove(index);
			
			int index2 = new Random().nextInt(MaterialData.size());
			MaterialData.get(index2).ItemDrops.put(config.material.toString(), "0.01");			
		}
		
		for (ConfigData configData : MaterialData)
		{
			file.MaterialData.put(configData.material.toString(), configData);
		}
		for (ConfigData configData : tools)
		{
			file.MaterialData.put(configData.material.toString(), configData);
		}
		
		return file;
	}
}
