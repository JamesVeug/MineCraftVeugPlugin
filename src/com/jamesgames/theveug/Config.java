package com.jamesgames.theveug;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

/**
 * Config file an admin can modify
 * 
 * @author james
 *
 */
public class Config
{
	private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	private HashMap<Material, String> itemDropChance = new HashMap<Material, String>();
	private HashMap<Material, String> itemDamageIncrease = new HashMap<Material, String>();
	private HashMap<Material, Long> materialToXP = new HashMap<Material, Long>();

	private String xpEquation;
	private Main plugin;

	public Config(Main plugin)
	{
		this.plugin = plugin;
		loadConfig();
	}

	public long GetXPFromMaterial(Material material)
	{
		if (materialToXP == null)
		{
			System.out.println("materialToXP is null wtf?");
		} else if (materialToXP.containsKey(material))
		{
			System.out.println("getting material op " + material);
			return materialToXP.get(material);
		}
		return 0L;
	}
	

	public long getExperienceForLevel(int level)
	{
		try
		{
			return solve(xpEquation.replace("-LEVEL-", String.valueOf(level))).longValue();
		} catch (ScriptException e)
		{
			plugin.getLogger().warning("An error occurred while attempting to solve config equation: \"" + xpEquation
					+ "\". Using default value instead (100 xp)");
			e.printStackTrace();
		}
		return 100L;
	}

	public int getDamageForLevel(Material type, int level, int damage, int defaultDamage)
	{
		if (!itemDamageIncrease.containsKey(type))
		{
			System.out.println("No Damage increase defind for: " + type.toString());
			return 0;
		}

		String equation = itemDamageIncrease.get(type);
		System.out.println("Getting damage for: " + equation + " at level " + level);
		equation = equation.replace("-LEVEL-", String.valueOf(level));
		equation = equation.replace("-DEFAULT_DAMAGE-", String.valueOf(defaultDamage));
		equation = equation.replace("-DAMAGE-", String.valueOf(damage));

		try
		{
			return solve(equation).intValue();
		} catch (ScriptException e)
		{
			plugin.getLogger().warning("An error occurred while attempting to solve damage equation: '" + equation
					+ "'. Using default damage instead (" + equation + ")");
			e.printStackTrace();
		}
		return defaultDamage;
	}

	public Number solve(String equation) throws ScriptException
	{
		return (Number) scriptEngine.eval(equation);
	}

	private Map<String, Object> getJsonConfig()
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
				Map<String, Object> defaultMap = getDefaults();

				// Convert the map to JSON format. There is a built in (de)serializer for it
				// already.
				String json = gson.toJson(defaultMap, new TypeToken<Map<String, Object>>() {
				}.getType());
				FileWriter writer = new FileWriter(jsonConfig);
				// Write to the file you passed
				writer.write(json);
				// Always close when done.
				writer.close();
			}

			// If the file exists (or we just made one exist), convert it from JSON format
			// to a populated Map object
			return gson.fromJson(new FileReader(jsonConfig), new TypeToken<Map<String, Object>>() {
			}.getType());
		} catch (IOException e)
		{
			// Print an error if something fails. Please use a real logger, not System.out.
			System.out.println("Error creating default configuration.");
		}

		return null;
	}

	private static Map<String, Object> getDefaults()
	{
		Map<String, Object> configData = new HashMap<String, Object>();

		// XPEquation
		configData.put("XPEquation", "-LEVEL- * 50");

		// BlockXPAmount
		Map<String, Object> blocks = new HashMap<String, Object>();

		// ItemDropRates
		Map<String, Object> itemDropChance = new HashMap<String, Object>();

		// ItemDamageIncrease
		Map<String, Object> itemDamageIncrease = new HashMap<String, Object>();

		String defaultLevelEquation = "-LEVEL- * 0.01";
		String defaultDamageEquation = "-DEFAULT_DAMAGE- * -LEVEL-";
		for (Material material : Material.values())
		{
			String m = material.toString().toLowerCase();

			// weapons
			if (m.endsWith("sword"))
			{
				itemDamageIncrease.put(material.toString(), defaultDamageEquation);
			}

			// tools
			if (m.endsWith("pickaxe"))
			{
				itemDamageIncrease.put(material.toString(), defaultDamageEquation);
			}
			if (m.endsWith("shovel"))
			{
				itemDamageIncrease.put(material.toString(), defaultDamageEquation);
			}
			if (m.endsWith("axe"))
			{
				itemDamageIncrease.put(material.toString(), defaultDamageEquation);
			}
			if (m.endsWith("hoe"))
			{
				itemDamageIncrease.put(material.toString(), defaultDamageEquation);
			}

			// ingots
			if (m.endsWith("ingot"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation);
			}

			// wool
			if (m.endsWith("wool"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation);
			}

			// ores
			if (m.endsWith("ore"))
			{
				blocks.put(material.toString(), 1);
			}

			// blocks of ore?
			if (m.endsWith("block"))
			{
				blocks.put(material.toString(), 1);
			}

			// armor
			if (m.endsWith("boots"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation);
			}
			if (m.endsWith("helmet"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation);
			}
			if (m.endsWith("leggings"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation);
			}
			if (m.endsWith("chestplate"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation);
			}

			if (m.endsWith("armour"))
			{
				itemDropChance.put(material.toString(), defaultLevelEquation); // horse armor
			}

			// default block types
			if (material == Material.STONE || material == Material.DIRT || material == Material.SAND
					|| material == Material.GRAVEL)
			{
				blocks.put(material.toString(), 1);
			}
		}

		configData.put("BlockXPAmount", blocks);
		configData.put("ItemDropRates", itemDropChance);
		configData.put("ItemDamageIncrease", itemDamageIncrease);
		return configData;
	}

	public boolean loadConfig()
	{
		Map<String, Object> config = getJsonConfig();
		if (config == null)
		{
			System.out.println("Config is null. Can not load config!");
			return false;
		}
		System.out.println("Config loaded: " + config.size());

		// XP Equation
		xpEquation = (String) config.get("XPEquation");
		System.out.println("XPEquation: " + xpEquation);

		// Item drop chances
		LinkedTreeMap<String, Object> itemEquations = (LinkedTreeMap<String, Object>) config.get("ItemDropRates");
		if (itemEquations != null)
		{
			System.out.println("ItemDropRates: " + itemEquations.size());
			for (String key : itemEquations.keySet())
			{
				Material itemMaterial = materialFromString(key);
				if (itemMaterial == Material.VOID_AIR)
					continue;

				String equation = (String) itemEquations.get(key);
				getItemDropChance().put(itemMaterial, equation);
			}
		}

		// Load XP amount for blocks
		LinkedTreeMap<String, Object> materialToXPData = (LinkedTreeMap<String, Object>) config.get("BlockXPAmount");
		if (materialToXPData != null)
		{
			System.out.println("BlockXPAmount: " + materialToXPData.size());
			for (String key : materialToXPData.keySet())
			{
				long amount = (long) Math.floor((double) materialToXPData.get(key));
				if (amount <= 0L)
				{
					plugin.getLogger().warning("xp value for material \"" + key + "\" is negative. Overriding to 0.");
					amount = 0L;
				}

				Material blockMaterial = materialFromString(key.toUpperCase());
				if (blockMaterial != Material.VOID_AIR)
				{
					materialToXP.put(blockMaterial, amount);
				}
			}
		}

		// Load Damage increase
		LinkedTreeMap<String, Object> itemDamage = (LinkedTreeMap<String, Object>) config.get("ItemDamageIncrease");
		if (itemDamage != null)
		{
			System.out.println("ItemDamageIncrease: " + itemDamage.size());
			for (String key : itemDamage.keySet())
			{
				Material itemMaterial = materialFromString(key);
				if (itemMaterial == Material.VOID_AIR)
					continue;

				String equation = (String) itemDamage.get(key);
				System.out.println("itemDamageIncrease: " + itemMaterial.toString() + " -> " + equation);
				itemDamageIncrease.put(itemMaterial, equation);
			}
		}
		
		return false;
	}

	private Material materialFromString(String val)
	{
		val = val.toUpperCase();
		String[] vals = val.split(";");

		try
		{
			return Material.valueOf(vals[0]);
		} catch (IllegalArgumentException ex)
		{
			plugin.getLogger().warning("Could not find material \"" + val + "\". Skipping this value.");
		}
		return Material.VOID_AIR;
	}

	public HashMap<Material, String> getItemDropChance()
	{
		return itemDropChance;
	}
}
