package com.jamesgames.theveug.Config;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;

/**
 * File representing the JSON file that can be modified
 * @author james
 *
 */
public class ConfigJSONFile
{
	public double XPRate;
	public double DropRate;
	public HashMap<String, ConfigData> MaterialData = new HashMap<String, ConfigData>();
	public ArrayList<String> LevelPhrases = new ArrayList<String>();
}
