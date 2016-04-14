package fr.pams.parcourManager;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParcourManager extends JavaPlugin
{
  
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(new EventClass(), this);
        EventClass.mapsecondes = new HashMap<Player, Integer>();
        EventClass.mapminutes = new HashMap<Player, Integer>();
        EventClass.task = new HashMap<Player, BukkitRunnable>();
        EventClass.mapchekpoint = new HashMap<Player, Location>();
       // EventClass.mapvie = new HashMap<Player, Integer>();
    }
    
}
