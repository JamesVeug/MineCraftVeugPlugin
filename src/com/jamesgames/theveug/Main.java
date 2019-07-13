package com.jamesgames.theveug;

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

	@Override
	public void onEnable()
	{
		getLogger().info("TheVeug onEnable has been invoked!");
		Config = new ConfigHandler(this);
		new LevelItemFactory(this);

		PluginManager pm = getServer().getPluginManager();
		TheVeugListener listener = new TheVeugListener(this);
		pm.registerEvents(listener, this);
	}

	@Override
	public void onDisable()
	{
		getLogger().info("TheVeug onDisable has been invoked!");
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
				this.getLogger().info("XP Rate now set to: " + rate);
				
				return true;
			}
			case "drop_rate":
			{
				float rate = Float.parseFloat(args[0]);
				Config.setDropRate(rate);
				this.getLogger().info("Drop Rate now set to: " + rate);
				
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
