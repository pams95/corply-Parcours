package fr.pams.parcourManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class EventClass implements Listener
{
    //Timer actionbar
    static HashMap<UUID, BukkitRunnable> task;
    static HashMap<UUID, Integer> mapminutes;
    static HashMap<UUID, Integer> mapsecondes;
    static BukkitRunnable runnable;
    static boolean canceller;
    
    static HashMap<UUID, Location> mapchekpoint;
    static HashMap<UUID, Integer> mapvie;
    static ArrayList<UUID> bParcour;
    Boolean onTime = true;
    // onMove
    boolean onCheckpoint = false;
    // onFall
    static int checkPoint;
    // saveCheckpoint
    boolean blocd�but = false;
    boolean bloc0 = true;
    boolean bloc1 = true;
    boolean bloc2 = true;
    boolean bloc3 = true;
    boolean bloc4 = true;
    boolean bloc5 = true;
    boolean blocFin = true;

    // Timer firework
    private int secondesf = 10;
    private int tachef;
   

    public void sendActionBar(final Player p)
    {
        if(!task.containsKey(p.getUniqueId()))
        {

            mapsecondes.put(p.getUniqueId(), 1);
            mapminutes.put(p.getUniqueId(), 0);
            task.put(p.getUniqueId(), runnable = new BukkitRunnable()
            {

                public void run()
                {
                    String actionBarS = "�6temps: �f" + mapminutes.get(p.getUniqueId()) + "�6 min �f" + mapsecondes.get(p.getUniqueId()) + "�6 sec !";
                    IChatBaseComponent actionBar = ChatSerializer.a("{\"text\": \"" + actionBarS + "\"}");
                    PacketPlayOutChat actionBarpacket = new PacketPlayOutChat(actionBar, (byte)2);
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(actionBarpacket);
                    if(mapsecondes.get(p.getUniqueId()) >= 0)
                    {
                        mapsecondes.put(p.getUniqueId(), mapsecondes.get(p.getUniqueId()) + 1);
                    }
                    if(mapsecondes.get(p.getUniqueId()) == 60)
                    {
                        mapsecondes.put(p.getUniqueId(), 0);
                        mapminutes.put(p.getUniqueId(), mapminutes.get(p.getUniqueId()) + 1);
                    }
                    if(mapminutes.get(p.getUniqueId()) == 15)
                    {
                        remove(p);
                    }
                    if(canceller)
                    {
                        canceller = false;
                        remove(p);
                    }

                }
            });
            task.get(p.getUniqueId()).runTaskTimer(Bukkit.getPluginManager().getPlugin("ParcourManager"), 0, 20);
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
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 95 && bParcour.contains(p.getUniqueId()) && onCheckpoint == true)
        {
            onCheckpoint = false;

        }
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 138 && bParcour.contains(p.getUniqueId()) && onCheckpoint == false)
        {
            onCheckpoint = true;
            checkPoint = 1;
            saveCheckpoint(p);
        }
        if(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId() == 133 && bParcour.contains(p.getUniqueId()) && onCheckpoint == false)
        {
            saveCheckpoint(p);

        }
        // 164/2/3/171:12/159:5
        if(bParcour.contains(p.getUniqueId()))
        {
            switch(loc.getBlock().getRelative(BlockFace.DOWN).getType().getId())
            {

                case 159:
                    teleportToCheckpoint(p);
                    break;
                case 164:
                    teleportToCheckpoint(p);
                    break;
                case 2:
                    teleportToCheckpoint(p);
                    break;
                case 3:
                    teleportToCheckpoint(p);
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onfallDamage(EntityDamageEvent e)
    {

        if(e.getEntity() instanceof Player)
        {
            Player p = (Player)e.getEntity();
            if(bParcour.contains(p.getUniqueId()) && e.getCause().equals(DamageCause.VOID) || e.getCause().equals(DamageCause.FALL))
            {
                teleportToCheckpoint(p);
                e.setCancelled(true);
            }
            else if(!(bParcour.contains(p.getUniqueId())) && e.getCause().equals(DamageCause.VOID))
            {
                World World = p.getWorld();
                Location spawn = new Location(World, 450.5, 157, 1156.5);
                p.teleport(spawn);
                e.setCancelled(true);
            }
        }
    }

    public void teleportToCheckpoint(Player p)
    {
        World World = p.getWorld();
        Location blockParcour = new Location(World, 450.5, 157, 1114.5);
        if(checkPoint == 0 && mapchekpoint.containsKey(p.getUniqueId()) && bParcour.contains(p.getUniqueId()) && task.containsKey(p.getUniqueId()) && mapvie.get(p.getUniqueId()) == 0)
        {
            p.teleport(blockParcour);
            p.sendMessage("�6[Parcours]: �fRecommence !");
            remove(p);
        }

        if(mapvie.get(p.getUniqueId()) > 1 && checkPoint == 1 && bParcour.contains(p.getUniqueId()) && mapchekpoint.containsKey(p.getUniqueId()))
        {
            p.teleport(mapchekpoint.get(p.getUniqueId()));
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) - 1);
            p.sendMessage("�6[Parcours]: �fIl ne vous reste que " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        else if(mapvie.get(p.getUniqueId()) == 1 && bParcour.contains(p.getUniqueId()) && mapchekpoint.containsKey(p.getUniqueId()))
        {
            p.sendMessage("�6[Parcours]: �fTu n'as plus de vie. Recommence !");
            remove(p);
            checkPoint = 0;
            p.teleport(blockParcour);
        }

    }

    public void saveCheckpoint(Player p)
    {
        World World = p.getWorld();
        Location blockParcour = new Location(World, 450.5, 157, 1109.5);
        Location spawnparcour = new Location(World, 450.5, 157, 1114.5);
        Location loc0 = new Location(World, 481.5, 166, 1102.5);
        Location loc1 = new Location(World, 521.5, 173, 1185.5);
        Location loc2 = new Location(World, 469.5, 189, 1187.5);
        Location loc3 = new Location(World, 414.5, 196, 1202.5);
        Location loc4 = new Location(World, 370.5, 158, 1157.5);
        Location loc5 = new Location(World, 440.5, 174, 1130.5);
        Location fin = new Location(World, 442.5, 185, 1156.5);
        double blockd�but = p.getLocation().distance(blockParcour);
        double blockDistance0 = p.getLocation().distance(loc0);
        double blockDistance1 = p.getLocation().distance(loc1);
        double blockDistance2 = p.getLocation().distance(loc2);
        double blockDistance3 = p.getLocation().distance(loc3);
        double blockDistance4 = p.getLocation().distance(loc4);
        double blockDistance5 = p.getLocation().distance(loc5);
        double blockfin = p.getLocation().distance(fin);
        if(blockd�but < 0.5 && blocd�but == false)
        {
            mapchekpoint.put(p.getUniqueId(), spawnparcour);
            mapvie.put(p.getUniqueId(), 0);
            p.sendMessage("�6[Parcours]: �fLe parcours commence, essaie de le faire le plus rapidement possible !");
            p.sendMessage("�6[Parcours]: �fLa commande /jump stop est faites pour pouvoir arr�ter le parcour � tout moment !");
            bParcour.add(p.getUniqueId());
            checkPoint = 0;
            bloc0 = false;
            bloc1 = true;
            bloc2 = true;
            bloc3 = true;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            sendActionBar(p);
        }
        if(blockDistance0 < 0.5 && bloc0 == false)
        {
            mapchekpoint.put(p.getUniqueId(), loc0);
            bloc0 = true;
            bloc1 = false;
            bloc2 = true;
            bloc3 = true;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("�6[Parcour]: �f1er checkpoint !");
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) + 3);
            p.sendMessage("�6[Parcour]: �fVous avez " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        if(blockDistance1 < 0.5 && bloc1 == false)
        {
            mapchekpoint.put(p.getUniqueId(), loc1);
            bloc1 = true;
            bloc2 = false;
            bloc3 = true;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("�6[Parcour]: �f2eme checkpoint !");
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) + 3);
            p.sendMessage("�6[Parcour]: �fVous avez " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        if(blockDistance2 < 0.5 && bloc2 == false)
        {
            mapchekpoint.put(p.getUniqueId(), loc2);
            bloc2 = true;
            bloc3 = false;
            bloc4 = true;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("�6[Parcour]: �f3eme checkpoint !");
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) + 3);
            p.sendMessage("�6[Parcour]: �fVous avez " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        if(blockDistance3 < 0.5 && bloc3 == false)
        {
            mapchekpoint.put(p.getUniqueId(), loc3);
            bloc3 = true;
            bloc4 = false;
            bloc5 = true;
            blocFin = true;
            p.sendMessage("�6[Parcour]: �f4eme checkpoint !");
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) + 3);
            p.sendMessage("�6[Parcour]: �fVous avez " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        if(blockDistance4 < 0.5 && bloc4 == false)
        {
            mapchekpoint.put(p.getUniqueId(), loc4);
            bloc4 = true;
            bloc5 = false;
            blocFin = true;
            p.sendMessage("�6[Parcour]: �f5eme checkpoint !");
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) + 3);
            p.sendMessage("�6[Parcour]: �fVous avez " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        if(blockDistance5 < 0.5 && bloc5 == false)
        {
            mapchekpoint.put(p.getUniqueId(), loc5);
            bloc5 = true;
            blocFin = false;
            p.sendMessage("�6[Parcour]: �f6eme checkpoint !");
            mapvie.put(p.getUniqueId(), mapvie.get(p.getUniqueId()) + 3);
            p.sendMessage("�6[Parcour]: �fVous avez " + mapvie.get(p.getUniqueId()) + " vies !");
        }
        if(blockfin < 0.5 && blocFin == false)
        {
            mapvie.remove(p.getUniqueId());
            Bukkit.broadcastMessage("�b" + p.getName() + "�a a r�ussis le jump en �b" + mapminutes.get(p.getUniqueId()) + "�a minutes et �b" + +mapsecondes.get(p.getUniqueId()) + "�a secondes !");
            timer(p);
            blocFin = true;
            remove(p);

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

    public static void remove(Player p)
    {
        // mapsecondes.remove(p.getUniqueId());
        // mapminutes.remove(p.getUniqueId());
        bParcour.remove(p.getUniqueId());
        task.get(p.getUniqueId()).cancel();
        task.remove(p.getUniqueId());
        mapchekpoint.remove(p.getUniqueId());
        mapvie.remove(p.getUniqueId());

    }
}
