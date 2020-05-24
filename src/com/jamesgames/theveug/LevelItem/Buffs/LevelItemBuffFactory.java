package com.jamesgames.theveug.LevelItem.Buffs;

import com.jamesgames.theveug.LevelItem.LevelItem;
import com.jamesgames.theveug.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Random;

public class LevelItemBuffFactory {

	public static LevelItemBuffFactory Instance;

	private ArrayList<ALevelItemBuff> buffTemplates;

	private Main plugin;
	
	public LevelItemBuffFactory(Main plugin)
	{
		Instance = this;
		this.plugin = plugin;

		buffTemplates = new ArrayList<>();
		buffTemplates.add(new MiningAreaBuff());
		buffTemplates.add(new RandomDropBuff());
		buffTemplates.add(new WeaponXPBuff());
		buffTemplates.add(new PlayerXPDropBuff());
		buffTemplates.add(new TunnelMiningBuff());
		buffTemplates.add(new AttackDamageBuff());
	}
	
	public ALevelItemBuff get(String rawLore) {
		String lore = ChatColor.stripColor(rawLore);
		for (int i = 0; i < buffTemplates.size(); i++) {
			ALevelItemBuff buff = buffTemplates.get(i);
			if(lore.startsWith(buff.buffName())){
				return buff.CreateFromLore(lore);
			}
		}


		Bukkit.broadcastMessage("[Debug] Could not find Buff with Lore: '" + lore + "'");
		return null;
	}

	public ALevelItemBuff getFromId(String id) {
		for (int i = 0; i < buffTemplates.size(); i++) {
			ALevelItemBuff buff = buffTemplates.get(i);
			if(id.equals(buff.ID())){
				return buff.CreateFromLore("");
			}
		}


		Bukkit.broadcastMessage("[Debug] Could not find Buff with id: '" + id + "'");
		return null;
	}

    public ALevelItemBuff CreateRandomBuff(LevelItem levelItem) {
		int index = new Random().nextInt(buffTemplates.size());
		ALevelItemBuff buff = buffTemplates.get(index);
		try {
			return buff.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
