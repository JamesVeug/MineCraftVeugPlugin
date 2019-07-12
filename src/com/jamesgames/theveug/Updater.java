package com.jamesgames.theveug;

import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Updater extends org.bukkit.scheduler.BukkitRunnable {
	HashSet<LevelItem> updateItems = new HashSet();

	private Updater() {}

	public void run() {
		/*for (LevelItem levelItem : updateItems) {
			try {
				ItemStack item = levelItem.getItem();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();
				int i = ToolLevels.this.get(lore);
				lore.set(i - 1, ChatColor.AQUA + "Level: " + levelItem.getLevel());
				lore.set(i, ChatColor.AQUA + "Experience: " + levelItem.getExperience() + "/"
						+ getExperienceForLevel(levelItem.getLevel()));
				meta.setLore(lore);
				item.setItemMeta(meta);
				if ((holder != null) && (holder.isOnline())) {
					int j = ToolLevels.this.getSlot(holder.getInventory(), levelItem);
					if (j == -1) {
						holder = null;
					} else
						holder.getInventory().setItem(j, item);
				} else {
					holder = null;
				}
			} catch (Exception ex) {
				ToolLevels.this.log("An error occurred while attempting to update an item. See stacktrace:");
				ex.printStackTrace();
			}
		}
		updateItems.clear();*/
	}
}
