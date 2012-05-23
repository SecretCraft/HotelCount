package basic;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

// Referenced classes of package basic:
//            BListener, config, SignManager

public class HotelCount extends JavaPlugin
{

    public static SignManager getSM()
    {
        return sman;
    }

    public static boolean canCreate(Player player)
    {
        return player.hasPermission("hotelcount.create") || isAdmin(player);
    }

    public static boolean isAdmin(Player player)
    {
        return player.hasPermission("hotelcount.admin") || player.isOp();
    }

    public static void saveAll()
    {
        config configuration = new config();
        configuration.save();
    }

    public static WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = server.getPluginManager().getPlugin("WorldGuard");
        if(plugin == null || !(plugin instanceof WorldGuardPlugin))
            return null;
        else
            return (WorldGuardPlugin)plugin;
    }
    
    public static HotelCount getInstance() {
    	return plugin;
    }

    public void onEnable()
    {
    	
    	 log = Logger.getLogger("Minecraft");
        
         error = false;
    	
    	plugin = this;
        server = getServer();
        sman = new SignManager();
        log.info("-HotelCount-:Hotelcount has been enabled!");
        agents = (new StringBuilder(String.valueOf(getDataFolder().getParent()))).append(File.separator).append("SimpleRegionMarket").append(File.separator).append("agents.yml").toString();
        signs = (new StringBuilder()).append(getDataFolder()).append(File.separator).toString();
        config configuration = new config();
        configuration.load();
        
        
        blockListener = new BListener();
        // TODO
        //server.getPluginManager().registerEvent(org.bukkit.event.Event.Type.SIGN_CHANGE, blockListener, org.bukkit.event.Event.Priority.Normal, this);
        //server.getPluginManager().registerEvent(org.bukkit.event.Event.Type.BLOCK_BREAK, blockListener, org.bukkit.event.Event.Priority.Normal, this);
        
        
        server.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            public void run()
            {
                HotelCount.getSM().updateAgents();
            }

            final HotelCount this$0;
            {
                this$0 = HotelCount.this;
                
            }
        }
, 20L, 1200L);
    }

    public void onDisable()
    {
        if(!error)
        {
            saveAll();
            log.info("-HotelCount-:Hotelcount has been disabled.");
        }
    }
    
    private static HotelCount plugin;
    private static Server server;
    public static String agents = null;
    public static String signs = null;
    public static SignManager sman;
    Logger log;
    private BListener blockListener;
    private boolean error;

}
