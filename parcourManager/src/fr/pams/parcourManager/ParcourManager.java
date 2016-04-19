package fr.pams.parcourManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParcourManager extends JavaPlugin
{

    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(new EventClass(), this);
        EventClass.mapsecondes = new HashMap<UUID, Integer>();
        EventClass.mapminutes = new HashMap<UUID, Integer>();
        EventClass.task = new HashMap<UUID, BukkitRunnable>();
        EventClass.mapchekpoint = new HashMap<UUID, Location>();
        EventClass.mapvie = new HashMap<UUID, Integer>();
        EventClass.bParcour = new ArrayList<UUID>();
        CommandExecutor ce = this;
        getCommand("test").setExecutor(ce);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player p = (Player)sender;
        World World = p.getWorld();
        Location spawnparcour = new Location(World, 329.5, 53, 79.5);
        if(!(sender instanceof Player))
        {
            p.sendMessage("vous n'êtes pas un joueur, vous ne pouvez pas éxecuter la commande.");
        }
        if(cmd.getName().equalsIgnoreCase("jump"))
        {
            if(!(args.length > 1))
            {
                if(args[0].equalsIgnoreCase("stop"))
                    if(EventClass.bParcour.contains(p.getUniqueId()))
                    {
                        p.teleport(spawnparcour);
                        EventClass.bParcour.remove(p.getUniqueId());
                        EventClass.mapchekpoint.remove(p.getUniqueId());
                        EventClass.checkPoint = 0;
                        EventClass.canceller = true;
                        EventClass.task.remove(p.getUniqueId());
                    }
                    else
                    {
                        p.sendMessage("§9[Parcours] Pour arrêter le parcours il faut déjà l'avoir commencer.");
                    }

            }
            else
            {
                p.sendMessage("/jump stop");
            }
        }

        return false;
    }
}
