package com.jamesgames.theveug.LevelItem.Buffs;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackDamageBuff extends ALevelItemBuff{

	@Override
	public ALevelItemBuff CreateFromLore(String lore) {
		AttackDamageBuff buff = new AttackDamageBuff();
		if(lore != null && !lore.isEmpty()) {
			String levelString = lore.substring(buff.buffName().length()).trim();
			buff.level = Integer.parseInt(levelString);
		}
		return buff;
	}

	@Override
    public String buffName() {
		return "Attack Damage";
	}

	@Override
	public String buffDescription() {
		return "Increases attack damage";
	}

	@Override
	public double getDamageBuff(EntityDamageByEntityEvent event) {
		return level;
	}
}
