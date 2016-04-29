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
        getCommand("jump").setExecutor(ce);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player p = (Player)sender;
        World World = p.getWorld();
        Location spawnparcour = new Location(World, 450.5, 157, 1114.5);
        if(!(sender instanceof Player))
        {
            p.sendMessage("vous n'êtes pas un joueur, vous ne pouvez pas éxecuter la commande.");
        }
        if(cmd.getName().equalsIgnoreCase("jump"))
        {
            if((args.length == 1))
            {
                if(args[0].equalsIgnoreCase("stop"))
                {
                    if(EventClass.bParcour.contains(p.getUniqueId()))
                    {
                        p.teleport(spawnparcour);
                        EventClass.checkPoint = 0;
                        EventClass.canceller = true;
                    }
                    else
                    {
                        p.sendMessage("Pour arrêter le parcours il faut au moins l'avoir commncé !");
                    }
                }
                else if(args[0].equalsIgnoreCase("help"))
                {
                    p.sendMessage("§6[Parcours] §fCette commande te permet d'arrêter le parcour à condition de l'avoir commencer !.");
                }
                else
                {
                    p.sendMessage("/jump stop");
                }

            }
            else if(args.length == 0)
            {
                p.sendMessage("/jump stop");
            }
            else
            {
                p.sendMessage("/jump stop");
            }
        }

        return false;
    }
 
}


