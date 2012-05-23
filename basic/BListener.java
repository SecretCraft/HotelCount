package basic;

import java.io.File;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

// Referenced classes of package basic:
//            HotelCount, SignManager, SingleSign

public class BListener implements Listener
{
	
	private HotelCount plugin;
	
    public BListener()
    {
    	plugin = HotelCount.getInstance();
        room = "";
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block b = event.getBlock();
        SingleSign agent = HotelCount.getSM().getSign(b.getLocation());
        if(agent == null)
            return;
        Player p = event.getPlayer();
        if(!HotelCount.isAdmin(p) && !HotelCount.canCreate(p))
        {
            event.setCancelled(true);
            ((Sign)b.getState()).update();
            return;
        }
        event.setCancelled(true);
        if(p != null)
            agent.destroyAgent(true);
        HotelCount.getSM().removeAgent(agent);
        HotelCount.saveAll();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if(!HotelCount.isAdmin(event.getPlayer()) && !HotelCount.canCreate(event.getPlayer()))
            return;
        if(event.getLine(0).equalsIgnoreCase("[Counter]"))
        {
            String region = event.getLine(1);
            int low = 0;
            int high = 0;
            try
            {
                low = Integer.parseInt(event.getLine(2));
            }
            catch(Exception exception) { }
            try
            {
                high = Integer.parseInt(event.getLine(3));
            }
            catch(Exception exception1) { }
            
            int count[] = getRooms((new StringBuilder()).append(event.getLine(1)).toString(), low, high);
            event.setLine(0, (new StringBuilder("Gesamt:")).append(count[0]).toString());
            event.setLine(1, (new StringBuilder("Frei:")).append(count[2]).toString());
            event.setLine(2, (new StringBuilder("Belegt:")).append(count[1]).toString());
            
            if(!room.equals(null))
                event.setLine(3, room);
            else
                event.setLine(3, "Kein freies Zimmer");
            room = "";
            HotelCount.getSM().addAgent(event.getBlock().getLocation(), region, low, high);
        }
    }

    public int[] getRooms(String name, int low, int high)
    {
        int i[] = new int[3];
        File ag = new File(HotelCount.agents);
        YamlConfiguration confighandle = YamlConfiguration.loadConfiguration(ag);
        for(Iterator iterator = confighandle.getKeys(false).iterator(); iterator.hasNext();)
        {
            String world = (String)iterator.next();
            ConfigurationSection path = confighandle.getConfigurationSection(world);
            for(Iterator iterator1 = path.getKeys(false).iterator(); iterator1.hasNext();)
            {
                String region = (String)iterator1.next();
                if(region.replaceAll("\\d*$", "").equalsIgnoreCase(name))
                    if(high > 0)
                    {
                        if(low <= Integer.parseInt(region.replaceAll("[^\\d]", "")) && high >= Integer.parseInt(region.replaceAll("[^\\d]", "")))
                        {
                            path = confighandle.getConfigurationSection(world).getConfigurationSection(region);
                            for(Iterator iterator2 = path.getKeys(false).iterator(); iterator2.hasNext();)
                            {
                                String signnr = (String)iterator2.next();
                                path = confighandle.getConfigurationSection(world).getConfigurationSection(region).getConfigurationSection(signnr);
                                if(path.isSet("Mode") && path.getInt("Mode") == 1)
                                {
                                    i[0]++;
                                    if(path.isSet("RentBy"))
                                    {
                                        i[1]++;
                                    } else
                                    {
                                        i[2]++;
                                        if(i[2] == 1)
                                            room = region;
                                        else
                                        if(Integer.parseInt(region.replaceAll("[^0-9]", "")) < Integer.parseInt(room.replaceAll("[^0-9]", "")))
                                            room = region;
                                    }
                                }
                            }

                        }
                    } else
                    {
                        path = confighandle.getConfigurationSection(world).getConfigurationSection(region);
                        for(Iterator iterator3 = path.getKeys(false).iterator(); iterator3.hasNext();)
                        {
                            String signnr = (String)iterator3.next();
                            path = confighandle.getConfigurationSection(world).getConfigurationSection(region).getConfigurationSection(signnr);
                            if(path.isSet("Mode") && path.getInt("Mode") == 1)
                            {
                                i[0]++;
                                if(path.isSet("RentBy"))
                                {
                                    i[1]++;
                                } else
                                {
                                    i[2]++;
                                    if(i[2] == 1)
                                        room = region;
                                    else
                                    if(Integer.parseInt(region.replaceAll("[^0-9]", "")) < Integer.parseInt(room.replaceAll("[^0-9]", "")))
                                        room = region;
                                }
                            }
                        }

                    }
            }

        }

        return i;
    }

    private String room;
}
