package com.jamesgames.theveug.LevelItem.Buffs;

import org.bukkit.event.block.BlockBreakEvent;

public abstract class ALevelItemBuff {
	public abstract ALevelItemBuff CreateFromLore(String lore);
	public abstract String buffName();
	public abstract String buffDescription();

	protected int level = 1;

	public int level() {
		return level;
	}

	public int maxLevel() {
		return 0;
	}

	public boolean canLevelUp() {
		return maxLevel() == 0 || level < maxLevel();
	}

	public void onBreak(BlockBreakEvent event) {
		
	}
	
	public double getDropRateBuff() {
		return 0.0D;
	}
	
	public double getXPRateBuff() {
		return 0.0D;
	}

	public String loreDescription() {
		return buffName() + " " + level;
	}

	public void levelUp() {
		level++;
	}
}
