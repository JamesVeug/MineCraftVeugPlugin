package com.jamesgames.theveug.LevelItem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.jamesgames.theveug.LevelItem.Buffs.ALevelItemBuff;
import com.jamesgames.theveug.LevelItem.Buffs.LevelItemBuffFactory;

public class LevelItem
{
	private final ItemStack item;
	private long id;
	private int level;
	private long xp;
	private long maxXP;
	private ArrayList<ALevelItemBuff> buffs;

	public LevelItem(ItemStack item, long id)
	{
		this.item = item;
		this.id = id;
		this.level = 1;
		this.xp = 0;
		this.maxXP = 0;
		this.buffs = new ArrayList<ALevelItemBuff>();
	}

	private void updateItemLore()
	{
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList();
		lore.clear();
		lore.add(ChatColor.AQUA + "Level: " + this.level);
		lore.add(ChatColor.AQUA + "XP: " + this.xp + "/" + this.maxXP);
		for (int i = 0; i < buffs.size(); i++) {
			ALevelItemBuff buff = buffs.get(i);
			lore.add(ChatColor.AQUA + buff.buffName() + " " + buff.level());
		}
		lore.add(ChatColor.RESET + "#ID" + this.id);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public ItemStack getItem()
	{
		return item;
	}

	public long getId()
	{
		return id;
	}

	public long getXP()
	{
		return xp;
	}

	public long getMaxXP()
	{
		return maxXP;
	}

	public void update(long xp, long maxXP, int level)
	{
		this.xp = xp;
		this.maxXP = maxXP;
		this.level = level;
		updateItemLore();
	}
	
	public void useLore(List<String> lore, boolean overwriteID) 
	{
		// Update from lore
		this.level = Integer.parseInt(ChatColor.stripColor(lore.get(0)).substring(7));
		
		int idIndex = lore.size() - 1;
		if(overwriteID) 
		{
			this.id = Integer.parseInt(ChatColor.stripColor(lore.get(idIndex)).substring(3));
		}
		
		int expectedIDIndex = 2;
		int totalBuffs = idIndex - expectedIDIndex; // We expect id to be at index 2 (being the last index)
		this.buffs.clear();
		for (int i = 0; i < totalBuffs; i++) {
			int index = expectedIDIndex + i;
			String data = lore.get(index);
			ALevelItemBuff buff = LevelItemBuffFactory.Instance.get(data);
			if(buff != null) {
				this.buffs.add(buff);
			}
		}

		
		String xpString = ChatColor.stripColor(lore.get(1)).substring(4);
		int length = xpString.indexOf("/");
		this.xp = TryParse(xpString.substring(0, length), 0L);
		this.maxXP = TryParse(xpString.substring(length + 1), 1L);
		
		// Update lore
		updateItemLore();
	}
	
	private Long TryParse(String s, Long defaultValue)
	{
		try
		{
			return Long.parseLong(s);
		}
		catch (NumberFormatException exception)
		{
			System.err.println(exception);
			return defaultValue;
		}
	}

	public int getLevel()
	{
		return level;
	}

	public void setId(long id)
	{
		this.id = id;
		
		// Update lore
		updateItemLore();
	}

	public void onBreak(BlockBreakEvent event) {
		for (int i = 0; i < buffs.size(); i++) {
			buffs.get(i).onBreak(event);
		}
	}

	public double getDropRateBuff() {
		double change = 1;
		for (int i = 0; i < buffs.size(); i++) {
			change += buffs.get(i).getDropRateBuff();
		}
		return change;
	}

	public double getXPRateBuff() {
		double change = 1;
		for (int i = 0; i < buffs.size(); i++) {
			change += buffs.get(i).getXPRateBuff();
		}
		return change;
	}

	public void refreshBuffs() {
		int expectedBuffs = level / 5;
		int totalBuffs = 0;
		for (int i = 0; i < buffs.size(); i++) {
			totalBuffs += buffs.get(i).level();
		}

		boolean edited = false;
		while(totalBuffs < expectedBuffs){
			// Get a random buff
			ALevelItemBuff newBuff = LevelItemBuffFactory.Instance.CreateRandomBuff(this);

			for (int i = 0; i < buffs.size(); i++) {
				ALevelItemBuff currentBuff = buffs.get(i);
				boolean buffAlreadyOwned = newBuff.buffName().equals(currentBuff.buffName());
				if(buffAlreadyOwned){
					if(currentBuff.canLevelUp()) {
						// Level our current buff
						currentBuff.levelUp();
						totalBuffs++;
						edited = true;
					}
					else{
						// Skip this buff
						continue;
					}
				}
				else if(i == buffs.size() - 1){
					// We don't have this buff. So add it
					buffs.add(newBuff);
					edited = true;
					totalBuffs++;
				}
			}
		}

		if(edited){
			updateItemLore();
		}
	}
}
