package com.jamesgames.theveug.LevelItem.Buffs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

public class PlayerXPDropBuff extends ALevelItemBuff{

	@Override
	public ALevelItemBuff CreateFromLore(String lore) {
		PlayerXPDropBuff buff = new PlayerXPDropBuff();
		String levelString = lore.substring(buff.buffName().length()).trim();
		buff.level = Integer.parseInt(levelString);
		return buff;
	}

	@Override
    public String buffName() {
		return "Player XP Drop";
	}

	@Override
	public String buffDescription() {
		return "Gives a chance to drop player xp when breaking blocks";
	}

	@Override
	public void onBreak(BlockBreakEvent event) {

		double dropChance = 0.1D * level;
		double randomChance = new Random().nextDouble();
		if(randomChance > dropChance){
			return;
		}

		int experience = new Random().nextInt(3) + 4; // between 3 and 6
		ExperienceOrb spawnEntity = (ExperienceOrb)event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB);
		spawnEntity.setExperience(experience);
	}
}
