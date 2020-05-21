package com.jamesgames.theveug;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.jamesgames.theveug.Config.ConfigHandler;
import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;
import com.jamesgames.theveug.LevelItem.LevelItemHandler;
import com.jamesgames.theveug.util.Util;

public class Main extends JavaPlugin
{
	public ConfigHandler Config;
	
	private RandomFactGenerator randomFactGenerator;
	
	private long TicksPerSecond = 20L;
	private boolean debugLogsEnabled;

	@Override
	public void onEnable()
	{
		getLogger().info("TheVeug onEnable has been invoked!");
		Config = new ConfigHandler(this);
		new LevelItemFactory(this);
		new LevelItemHandler(this);
		randomFactGenerator = new RandomFactGenerator(this);
		randomFactGenerator.runTaskTimer(this, 0L, 10 * 60 * TicksPerSecond);
		

		PluginManager pm = getServer().getPluginManager();
		TheVeugListener listener = new TheVeugListener(this);
		pm.registerEvents(listener, this);
	}

	@Override
	public void onDisable()
	{
		getLogger().info("TheVeug onDisable has been invoked!");
		randomFactGenerator.cancel();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		switch (command.getName())
		{
			case "reload_config":
			{
				if (Config.loadConfig())
				{
					Bukkit.broadcastMessage("TheVeug Config has been reloaded!");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "TheVeug Config failed to reload..." + ChatColor.RESET);
				}
				return true;
			}
			case "xp_rate":
			{
				float rate = Float.parseFloat(args[0]);
				Config.setXPRate(rate);
				Bukkit.broadcastMessage("XP Rate now set to: " + rate);
				
				return true;
			}
			case "drop_rate":
			{
				float rate = Float.parseFloat(args[0]);
				Config.setDropRate(rate);
				Bukkit.broadcastMessage("Drop Rate now set to: " + rate);
				
				return true;
			}
			case "toggle_debug_logs":
			{
				debugLogsEnabled = Boolean.parseBoolean(args[0]);
				if(debugLogsEnabled)
					Bukkit.broadcastMessage("Debug Logs are now toggled.");
				else
					Bukkit.broadcastMessage("Debug Logs are now disabled.");
				
				return true;
			}
			case "what_drops":
			{
				Material material = Util.ToMaterial(args[0]);
				ArrayList<Material> droppedByList = Config.getMaterialsDroppedBy(material);
				if(droppedByList == null) {
					sender.sendMessage(ChatColor.RED + "Unable to get drops for " + args[0] + ChatColor.RESET);
				}
				else if(droppedByList.size() == 0) {
					String responseMessage = Util.getMaterialName(material) + " is not dropped by anything.";
					sender.sendMessage(ChatColor.GREEN + responseMessage + ChatColor.RESET);
				}
				else {
					String responseMessage = Util.getMaterialName(material) + " is dropped by:";
					for(int i = 0; i < droppedByList.size(); i++) {
						responseMessage += "\n  " + Util.getMaterialName(droppedByList.get(i));
					}
					sender.sendMessage(ChatColor.GREEN + responseMessage + ChatColor.RESET);			
				}

				
				return true;
			}
			case "what_drops_from":
			{
				Material material = Util.ToMaterial(args[0]);
				ImportedData data = Config.GetDataForMaterial(material);
				if(data == null || data.ItemDrops == null) {
					sender.sendMessage(ChatColor.GREEN + "Unable to get drops of " + args[0] + ChatColor.RESET);
				}
				else if(data.ItemDrops.size() == 0) {
					String responseMessage = Util.getMaterialName(material) + " does not dropp by anything.";
					sender.sendMessage(ChatColor.GREEN + responseMessage + ChatColor.RESET);
				}
				else {
					String responseMessage = Util.getMaterialName(material) + " drops:";
					for(int i = 0; i < data.ItemDrops.size(); i++) {
						responseMessage += "\n  " + Util.getMaterialName(data.ItemDrops.get(i).material);
					}
					sender.sendMessage(ChatColor.GREEN + responseMessage + ChatColor.RESET);			
				}

				
				return true;
			}
			default:
			{
				sender.sendMessage("Unknown command '" + command.getName() + "'");
			}
		}

		return false;
	}
	
	public void Log(String message)
	{
		if (!debugLogsEnabled) return;

		Bukkit.broadcastMessage("[Debug] " + message);
	}

	public void Log(String message, Object... params)
	{
		if (!debugLogsEnabled) return;

		Log(String.format(message, params));
	}
}
