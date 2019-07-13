package com.jamesgames.theveug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.jamesgames.theveug.Config.ConfigHandler;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;

public class Main extends JavaPlugin
{
	public ConfigHandler Config;
	
	private RandomFactGenerator randomFactGenerator;
	
	private long TicksPerSecond = 20L;

	@Override
	public void onEnable()
	{
		getLogger().info("TheVeug onEnable has been invoked!");
		Config = new ConfigHandler(this);
		new LevelItemFactory(this);
		randomFactGenerator = new RandomFactGenerator(this);
		randomFactGenerator.runTaskTimer(this, 0L, 5 * TicksPerSecond);
		

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
					sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully!." + ChatColor.RESET);
				}
				else
				{
					sender.sendMessage(ChatColor.GREEN + "Config failed to reloaded..." + ChatColor.RESET);
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
			default:
			{
				sender.sendMessage("Unknown command '" + command.getName() + "'");
			}
		}

		return false;
	}
}
