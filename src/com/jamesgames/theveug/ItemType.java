package com.jamesgames.theveug;

import org.bukkit.Material;

public class ItemType
{
	private Material type;
	private short data;

	public ItemType(Material type, short data)
	{
		this.type = type;
		this.data = data;
	}

	public static ItemType get(String val)
	{
		val = val.toUpperCase();
		String[] vals = val.split(";");
		Material material = Material.valueOf(vals[0]);
		short data = 0;
		if (vals.length > 1)
			data = Short.valueOf(vals[1]).shortValue();
		return new ItemType(material, data);
	}

	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if ((o == null) || (getClass() != o.getClass()))
		{
			return false;
		}
		ItemType type1 = (ItemType) o;

		return (getData() == type1.getData()) && (type == type1.type);
	}

	public int hashCode()
	{
		int result = type.hashCode();
		result = 31 * result + getData();
		return result;
	}

	public Material getMaterial()
	{
		return type;
	}

	public short getData()
	{
		return data;
	}
}