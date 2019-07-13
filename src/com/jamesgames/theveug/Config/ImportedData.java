package com.jamesgames.theveug.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import com.jamesgames.theveug.util.Util;

public class ImportedData
{
	public class ImportedDataDrop
	{
		public Material material;
		public String Equation;

		public ImportedDataDrop(Material material, String equation)
		{
			this.material = material;
			this.Equation = equation;
		}
	}
	
	public Material material;
	public long XPReward = 0;
	public List<ImportedDataDrop> ItemDrops;
	public String LevelXPEquation = null;
	public int RandomAmountMin = 1; // Inclusive
	public int RandomAmountMax = 1; // Inclusive

	public ImportedData(Material material, ConfigData data)
	{
		this.material = material;
		this.XPReward = data.XPReward;
		this.LevelXPEquation = data.LevelXPEquation;
		this.RandomAmountMin = data.RandomAmountMin;
		this.RandomAmountMax = data.RandomAmountMax;
		
		ItemDrops = new ArrayList<ImportedDataDrop>(data.ItemDrops.size());
		for (String materialString : data.ItemDrops.keySet())
		{
			Material dropMaterial = Util.ToMaterial(materialString);
			if (dropMaterial == Material.VOID_AIR)
			{
				System.out.println("Unknown Material type: " + materialString + ". skipping drop from "
						+ material.toString() + ".");
				continue;
			}
			

			String value = data.ItemDrops.get(materialString);
			this.ItemDrops.add(new ImportedDataDrop(dropMaterial, value));
		}
	}
}
