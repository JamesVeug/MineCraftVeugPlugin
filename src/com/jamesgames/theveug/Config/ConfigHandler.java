package com.jamesgames.theveug.Config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.jamesgames.theveug.Main;
import com.jamesgames.theveug.util.Util;

/**
 * Config file an admin can modify
 * 
 * @author james
 *
 */
public class ConfigHandler
{
	private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	private HashMap<Material, ImportedData> materialData = new HashMap<Material, ImportedData>();
	private HashMap<EntityType, ImportedData> entityTypeData = new HashMap<EntityType, ImportedData>();
	private ArrayList<String> levelUpMessages = new ArrayList<String>();

	private double xpRate;
	private double dropRate;
	private Main plugin;

	public ConfigHandler(Main plugin)
	{
		this.plugin = plugin;
		loadConfig();
	}
	
	public ImportedData GetDataForMaterial(Material material)
	{
		if (materialData == null)
		{
			System.out.println("materialData is null wtf?");
		}
		else if (materialData.containsKey(material))
		{
			return materialData.get(material);
		}

		return null;
	}
	
	public ImportedData GetDataForEntityType(EntityType entityType)
	{
		if (entityTypeData == null)
		{
			System.out.println("entityTypeData is null wtf?");
		}
		else if (entityTypeData.containsKey(entityType))
		{
			return entityTypeData.get(entityType);
		}

		return null;
	}
	

	public long getMaxXPForLevel(Material material, int level)
	{
		ImportedData data = GetDataForMaterial(material);
		if (data == null) {

			System.out.println("No data specified for Material: " + material.toString());
			return 0L;
		}
		if (!data.canLevelUp()) 
		{
			System.out.println("No LevelXPEquation specified for Material: " + material.toString());
			return 0L;
		}
		
		try
		{
			return solve(data.LevelXPEquation.replace("-LEVEL-", String.valueOf(level))).longValue();
		} 
		catch (ScriptException e)
		{
			plugin.getLogger().warning("An error occurred while attempting to solve config equation: \"" + data.LevelXPEquation
					+ "\". Using default value instead (100 xp)");
			e.printStackTrace();
		}
		return 100L;
	}

	/*public int getDamageForLevel(Material type, int level, int damage, int defaultDamage)
	{
		ImportedData data = GetDataForMaterial(type);
		if (data == null) {
			return 0;
		}

		String equation = data.LevelDamageEquation;
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
	}*/

	public Number solve(String equation) throws ScriptException
	{
		return (Number)scriptEngine.eval(equation);
	}

	public boolean loadConfig()
	{
		ConfigJSONFile config = new ConfigLoader().getJsonConfig();
		if (config == null)
		{
			System.out.println("Config is null. Can not load config!");
			return false;
		}
		System.out.println("Config loaded");

		// Global rates
		xpRate = config.XPRate;
		dropRate = config.DropRate;
		System.out.println("XPRate: " + getXpRate());
		System.out.println("DropRate: " + getDropRate());

		// Level up messages
		levelUpMessages = config.LevelPhrases;
		
		materialData.clear();
		for (String materialString : config.MaterialData.keySet())
		{
			Material material = Util.ToMaterial(materialString);
			EntityType entityType = Util.ToEntityType(materialString);
			if (material == Material.VOID_AIR && entityType == EntityType.UNKNOWN) 
			{
				System.out.println("Unknown Material type: " + materialString + ". skipping from config.");								
				continue;
			}
			
			ImportedData data = new ImportedData(material, entityType, config.MaterialData.get(materialString));
			if (material != Material.VOID_AIR)
				materialData.put(material, data);
			else if (entityType != EntityType.UNKNOWN)
				entityTypeData.put(entityType, data);
		}
		
		return true;
	}

	public double getXpRate()
	{
		return xpRate;
	}

	public double getDropRate()
	{
		return dropRate;
	}

	public void setXPRate(double rate)
	{
		xpRate = rate;
	}

	public void setDropRate(double rate)
	{
		dropRate = rate;
	}
	
	public String RandomLevelUpMessage() 
	{
		return levelUpMessages.get(new Random().nextInt(levelUpMessages.size()));
	}
}
