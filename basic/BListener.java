package basic;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
            
            int count[] = HotelCount.getSM().getRooms(event.getLine(1), low, high, event.getBlock().getWorld().getName());
            event.setLine(0, (new StringBuilder("Gesamt: ")).append(count[SignManager.ALL]).toString());
            event.setLine(1, (new StringBuilder("Frei  : ")).append(count[SignManager.FREE]).toString());
            event.setLine(2, (new StringBuilder("Belegt: ")).append(count[SignManager.TAKEN]).toString());
            
            if(!HotelCount.getSM().freeRoom.isEmpty())
                event.setLine(3, HotelCount.getSM().freeRoom);
            else
                event.setLine(3, "No free room"); // auf deutsch passt es nicht aufs schild!
            HotelCount.getSM().addAgent(event.getBlock().getLocation(), region, low, high);
        }
    }
}
