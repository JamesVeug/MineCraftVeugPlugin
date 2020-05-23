package com.jamesgames.theveug.LevelItem.Buffs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class RandomDropBuff extends ALevelItemBuff{

	@Override
	public ALevelItemBuff CreateFromLore(String lore) {
		RandomDropBuff buff = new RandomDropBuff();
		String levelString = lore.substring(buff.buffName().length()).trim();
		buff.level = Integer.parseInt(levelString);
		return buff;
	}

	@Override
    public String buffName() {
		return "Random Drop Buff";
	}

	@Override
	public String buffDescription() {
		return "Increases chance of dropping a random item";
	}
	
	public double getDropRateBuff() {
		return 0.05D * level;
	}
}
