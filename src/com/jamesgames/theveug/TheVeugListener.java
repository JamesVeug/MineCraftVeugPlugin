package com.jamesgames.theveug;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.script.ScriptException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.LevelItem.LevelItem;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;
import com.jamesgames.theveug.LevelItem.LevelItemHandler;
import com.jamesgames.theveug.util.Util;

public class TheVeugListener implements Listener
{

	private Main plugin;

	public TheVeugListener(Main plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String name = player.getDisplayName();
		player.sendMessage("Hello " + name + "!");
	}
	
	@EventHandler
    public void onKill(EntityDeathEvent e) {        
        Entity deadThing = e.getEntity();
		plugin.Log("Killed " + deadThing.getType().toString());
		if (deadThing instanceof Player)
			return;
		
        Entity killer = e.getEntity().getKiller();
        if(killer == null) {
        	return; // Died naturally
        }
        
        plugin.Log("Killer " + killer.getType().toString());
		if (!(killer instanceof Player))
			return;
		
		Player player = (Player)killer;
		ItemStack itemInHand = player.getItemInHand();
        plugin.Log("Weapon " + itemInHand.getType().toString());
        
		// Make sure this item can level up
		ImportedData weaponData = plugin.Config.GetDataForMaterial(itemInHand.getType());
		if(weaponData == null || !weaponData.canLevelUp())
		{
	        plugin.Log("No Data for weapon " + itemInHand.getType().toString());
			return;
		}

		// Make sure the killed thing has data
        plugin.Log("Got Weapon Data");
		EntityType entityType = deadThing.getType();
		ImportedData mobData = plugin.Config.GetDataForEntityType(deadThing.getType());
		if(mobData == null) 
		{
			plugin.Log("No data found for type " + deadThing.getType().toString());
			return;
		}
		else if(mobData.XPReward <= 0) 
		{
			plugin.Log(deadThing.getType().toString() + " Rewards no XP");
			return;
		}

        plugin.Log("Got Entity Data");

		// Create new item
		LevelItem levelItem = LevelItemFactory.Instance.get(itemInHand);
		if(levelItem != null) {
			// Add XP
			if(levelItem.getMaxXP() > 0) {
				LevelItemHandler.Instance.AddExperience(levelItem, mobData.XPReward, player);
			}

			// Create drop
			TheVeug.CreateDrop(mobData, deadThing.getWorld(), deadThing.getLocation(), levelItem);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event)
	{
		// Create new item
		Player player = event.getPlayer();
		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		LevelItem levelItem = LevelItemFactory.Instance.get(itemInHand);
		if (levelItem == null)
		{
			return;
		}

		ImportedData blockData = plugin.Config.GetDataForMaterial(event.getBlock().getType());
		if (blockData != null) 
		{
			// Give XP
			long rewardedXP = (long) Math.ceil(blockData.XPReward * levelItem.getXPRateBuff());
			TheVeug.AwardWeaponXP(player, rewardedXP, levelItem);

			// Create drop
			TheVeug.CreateDrop(blockData, event.getBlock().getWorld(), event.getBlock().getLocation(), levelItem);
		}
		
		// Use buffs
		levelItem.onBreak(event);
	}
}
