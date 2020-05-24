package com.jamesgames.theveug.LevelItem.Buffs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class WeaponXPBuff extends ALevelItemBuff{

	@Override
	public ALevelItemBuff CreateFromLore(String lore) {
		WeaponXPBuff buff = new WeaponXPBuff();
		if(lore != null && !lore.isEmpty()) {
			String levelString = lore.substring(buff.buffName().length()).trim();
			buff.level = Integer.parseInt(levelString);
		}
		return buff;
	}

	@Override
    public String buffName() {
		return "Weapon XP Buff";
	}

	@Override
	public String buffDescription() {
		return "Increases xp earned when mining";
	}
	
	public double getXPRateBuff() {
		return 0.05D * level;
	}
}
