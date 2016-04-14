package fr.pams.parcourManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class EventClass implements Listener
{
    static HashMap<Player, BukkitRunnable> task;
    static HashMap<Player, Integer> mapminutes;
    static HashMap<Player, Integer> mapsecondes;
    static HashMap<Player, Location> mapchekpoint;
    static HashMap<Player, Integer> mapvie;
    static ArrayList<UUID> bParcour;
    BukkitRunnable runnable;
    int i = 0;
    int début;
    Boolean onTime = true;
    // onMove
    int millieu;
    boolean onCheckpoint = false;
    // onFall
    static int checkPoint;
    // saveCheckpoint
    boolean blocdébut;
    boolean bloc0;
    boolean bloc1;
    boolean bloc2;
    boolean bloc3;
    boolean bloc4;
    boolean bloc5;
    boolean blocFin;

    // Timer
    private int secondesf = 10;
    private int tachef;
    static boolean canceller;

    @SuppressWarnings("deprecation")
    public void sendActionBar(final Player p)
    {
        if(!task.containsKey(p))
        {

            mapsecondes.put(p, 1);
            mapminutes.put(p, 0);
            task.put(p, runnable = new BukkitRunnable()
            {
                public void run()
                {
                    String actionBarS = "temps: " + ChatColor.GOLD + mapminutes.get(p) + ChatColor.BLACK + " min " + ChatColor.GOLD + mapsecondes.get(p) + " sec !";
                    IChatBaseComponent actionBar = ChatSerializer.a("{\"text\": \"" + actionBarS + "\"}");
                    PacketPlayOutChat actionBarpacket = new PacketPlayOutChat(actionBar, (byte)2);
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(actionBarpacket);
                    if(mapsecondes.get(p) >= 0)
                    {
                        mapsecondes.put(p, mapsecondes.get(p) + 1);

                    }
                    if(mapsecondes.get(p) == 60)
                    {
                        mapsecondes.put(p, 0);
                        mapminutes.put(p, mapminutes.get(p) + 1);
                    }
                    if(mapminutes.get(p) == 10)
                    {
                        task.remove(p);
                        runnable.cancel();
                    }
                    if(onTime = false)
                    {
                        runnable.cancel();
                        task.remove(p);
                    }
                    if(canceller)
                    {
                        canceller = false;
                        cancel();
                    }

                }
            });
            task.get(p).runTaskTimer(Bukkit.getPluginManager().getPlugin("ParcourManager"), 0, 20);
        }

    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType() == org.bukkit.Material.SEA_LANTERN && !(bParcour.contains(p.getUniqueId())))
        {
            saveCheckpoint(p);

        }
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 138 && bParcour.contains(p.getUniqueId()) && onCheckpoint == false)
        {
            onCheckpoint = true;
            début = 1;
            checkPoint = 1;
            saveCheckpoint(p);
        }
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 95 && bParcour.contains(p.getUniqueId()) && onCheckpoint == true)
        {
            onCheckpoint = false;

        }
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 133 && bParcour.contains(p.getUniqueId()) && onCheckpoint == false)
        {
            saveCheckpoint(p);

        }
    }

    @EventHandler
    public void onfallDamage(EntityDamageEvent e)
    {

        if(e.getEntity() instanceof Player)
        {

            Player p = (Player)e.getEntity();

            World w = p.getWorld();

            if(e.getCause().equals(DamageCause.VOID) || e.getCause().equals(DamageCause.FALL))
            {
                teleportToCheckpoint(p);
                e.setCancelled(true);

            }
        }
    }

    public void teleportToCheckpoint(Player p)
    {
        if(début == 0 && mapchekpoint.containsKey(p))
        {
            p.teleport(mapchekpoint.get(p));
            bParcour.remove(p.getUniqueId());
            mapchekpoint.remove(p);
            checkPoint = 0;
            runnable.cancel();
            task.remove(p);
        }

        if(mapvie.get(p) >= 1 && checkPoint == 1 && bParcour.contains(p.getUniqueId()) && mapchekpoint.containsKey(p))
        {
            p.teleport(mapchekpoint.get(p));
            mapvie.put(p, mapvie.get(p) - 1);
            p.sendMessage("§9[Parcour]: Il ne vous reste que " + mapvie.get(p) + " vies");
        }
        else if(mapvie.get(p) >= 1 && checkPoint == 1 && !(bParcour.contains(p.getUniqueId())) && mapchekpoint.containsKey(p))
        {

            World World = p.getWorld();
            Location blockParcour = new Location(World, 327.5, 53, 79.5);
            bParcour.remove(p.getUniqueId());
            task.remove(p);
            mapchekpoint.remove(p);
            runnable.cancel();
            checkPoint = 0;
            p.teleport(blockParcour);
            p.sendMessage("§9Tu n'as plus de vie :(");
        }
        else
        {
            World World = p.getWorld();
            Location blockParcour = new Location(World, 327.5, 53, 79.5);
            bParcour.remove(p.getUniqueId());
            task.remove(p);
            mapchekpoint.remove(p);
            runnable.cancel();
            checkPoint = 0;
            p.teleport(blockParcour);
        }
    }

    @SuppressWarnings("unused")
    public void saveCheckpoint(Player p)
    {

        World World = p.getWorld();
        Location blockParcour = new Location(World, 325.5, 53, 79.5);
        Location spawnparcour = new Location(World, 329.5, 53, 79.5);
        Location loc0 = new Location(World, 300.5, 62, 86.5);
        Location loc1 = new Location(World, 318.5, 66, 91.5);
        Location loc2 = new Location(World, 169.5, 189, 1187.5);
        Location loc3 = new Location(World, 414.5, 196, 1202.5);
        Location loc4 = new Location(World, 370.5, 158, 1157.5);
        Location loc5 = new Location(World, 440.5, 174, 1130.5);
        Location fin = new Location(World, 315.5, 72, 84.5);
        double blockdébut = p.getLocation().distance(blockParcour);
        double blockDistance0 = p.getLocation().distance(loc0);
        double blockDistance1 = p.getLocation().distance(loc1);
        double blockDistance2 = p.getLocation().distance(loc2);
        double blockDistance3 = p.getLocation().distance(loc3);
        double blockDistance4 = p.getLocation().distance(loc4);
        double blockDistance5 = p.getLocation().distance(loc5);
        double blockfin = p.getLocation().distance(fin);
        if(blockdébut < 1 && blockdébut > 0 && blocdébut == false)
        {
            mapchekpoint.put(p, spawnparcour);
            mapvie.put(p, 0);
            p.sendMessage("§9[Parcours]: Le parcoure commence, essaie de le faire le plus rapidement possible !");
            p.sendMessage("§9[Parcours]: La commande /Test est faites pour pouvoir arrêter le parcour à tout moment :)");
            bParcour.add(p.getUniqueId());
            début = 0;
            bloc0 = false;
            bloc1 = true;
            bloc2 = true;
            bloc3 = true;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            sendActionBar(p);
        }
        if(blockDistance0 < 1 && blockDistance0 > 0 && bloc0 == false)
        {
            mapchekpoint.put(p, loc0);
            bloc0 = true;
            bloc1 = false;
            bloc2 = true;
            bloc3 = true;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("§9[Parcour]: 1er checkpoint !");
            mapvie.put(p, mapvie.get(p) + 3);
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
        if(blockDistance1 < 1 && blockDistance1 > 0 && bloc1 == false)
        {
            mapchekpoint.put(p, loc1);
            bloc1 = true;
            bloc2 = false;
            bloc3 = true;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("§9[Parcour]: 2eme checkpoint !");
            mapvie.put(p, mapvie.get(p) + 3);
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
        if(blockDistance2 < 1 && blockDistance2 > 0 && bloc2 == false)
        {
            mapchekpoint.put(p, loc2);
            bloc2 = true;
            bloc3 = false;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("§9[Parcour]: 3eme checkpoint !");
            mapvie.put(p, mapvie.get(p) + 3);
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
        if(blockDistance3 < 1 && blockDistance3 > 0 && bloc3 == false)
        {
            mapchekpoint.put(p, loc3);
            bloc3 = true;
            bloc4 = false;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("§9[Parcour]: 4eme checkpoint !");
            mapvie.put(p, mapvie.get(p) + 3);
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
        if(blockDistance4 < 1 && blockDistance4 > 0 && bloc4 == false)
        {
            mapchekpoint.put(p, loc4);
            bloc4 = true;
            bloc5 = false;
            blocFin = true;
            p.sendMessage("§9[Parcour]: 5eme checkpoint !");
            mapvie.put(p, mapvie.get(p) + 3);
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
        if(blockDistance5 < 1 && blockDistance5 > 0 && bloc5 == false)
        {
            mapchekpoint.put(p, loc5);
            bloc5 = true;
            blocFin = false;
            p.sendMessage("§9[Parcour]: 6eme checkpoint !");
            mapvie.put(p, mapvie.get(p) + 3);
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
        if(blockfin < 1 && blockfin > 0 && blocFin == false)
        {
            mapvie.put(p, 0);
            blocFin = true;
            runnable.cancel();
            task.remove(p);
            bParcour.remove(p.getUniqueId());
            Bukkit.broadcastMessage(ChatColor.AQUA + p.getName() + ChatColor.GREEN + " a réussis le jump en " + ChatColor.AQUA + mapminutes.get(p) + ChatColor.GREEN + " minutes et " + ChatColor.AQUA + mapsecondes.get(p) + ChatColor.GREEN + " secondes");
            p.sendMessage("§9[Parcour]: Vous avez " + mapvie.get(p) + " vies");
        }
    }

    public void Firework(Player p)
    {

        Firework f = (Firework)p.getWorld().spawn(p.getLocation().add(1D, 0D, 0D), Firework.class);

        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.BLUE).withFade(Color.GREEN).build());
        fm.setPower(1);
        f.setFireworkMeta(fm);

        Firework f1 = (Firework)p.getWorld().spawn(p.getLocation().add(0D, 0D, 1D), Firework.class);
        FireworkMeta fm1 = f.getFireworkMeta();
        fm1.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.GREEN).withFade(Color.GREEN).build());
        fm1.setPower(1);
        f1.setFireworkMeta(fm1);

        Firework f2 = (Firework)p.getWorld().spawn(p.getLocation().add(-1D, 0D, -0D), Firework.class);
        FireworkMeta fm2 = f.getFireworkMeta();
        fm2.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.RED).withFade(Color.GREEN).build());
        fm2.setPower(1);
        f2.setFireworkMeta(fm2);

        Firework f3 = (Firework)p.getWorld().spawn(p.getLocation().add(-0D, 0D, -1D), Firework.class);
        FireworkMeta fm3 = f.getFireworkMeta();
        fm3.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.YELLOW).withFade(Color.GREEN).build());
        fm3.setPower(1);
        f3.setFireworkMeta(fm3);

    }

    public void timer(final Player p)
    {
        tachef = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("ParcourManager"), new Runnable()
        {
            @Override
            public void run()
            {
                if(secondesf <= 10)
                {
                    secondesf--;
                    Firework(p);
                }
                if(secondesf <= 1)
                {
                    secondesf = 10;
                    Bukkit.getScheduler().cancelTask(tachef);
                }
            }
        }, 0, 10);
    }
}
