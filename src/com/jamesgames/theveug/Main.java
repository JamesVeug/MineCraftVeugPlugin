package com.jamesgames.theveug;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	public Config Config;

	@Override
	public void onEnable()
	{
		getLogger().info("TheVeug onEnable has been invoked!");
		Config = new Config(this);
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args[0].equalsIgnoreCase("reloadconfig"))
		{
			if(Config.loadConfig()) {
				sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully!." + ChatColor.RESET);
			}
			else {
				sender.sendMessage(ChatColor.GREEN + "Config failed to reloaded..." + ChatColor.RESET);
			}
		}
		
		return true;
	}
}
