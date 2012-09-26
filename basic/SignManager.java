package basic;

import java.io.File;
import java.util.*;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

// Referenced classes of package basic:
//            SingleSign, HotelCount

public class SignManager
{

    public SignManager()
    {
        allSigns = new ArrayList<SingleSign>();
    }

    public SingleSign addAgent(Location location, String regionID)
    {
        return addAgent(location, regionID, 0, 0);
    }

    public SingleSign addAgent(Location location, String regionID, int low, int high)
    {
        if(location != null) {
            SingleSign newSign = new SingleSign(location, regionID, low, high);
            getAllSigns().add(newSign);
            return newSign;
        }
        return null;
    }

    public ArrayList<SingleSign> getAllSigns()
    {
        return allSigns;
    }

    public void updateAgents()
    {
        Sign agentsign = null;
        
        for(Iterator<SingleSign> iterator = allSigns.iterator(); iterator.hasNext(); agentsign.update())
        {
            SingleSign obj = iterator.next();
            int rooms[] = getRooms(obj.getRegionID(), obj.getLow(), obj.getHigh());
            
            if ( obj.getLocation().getBlock().getState() instanceof Sign ) {
            	agentsign = (Sign)obj.getLocation().getBlock().getState();     
                
                agentsign.setLine(0, (new StringBuilder("Gesamt: ")).append(rooms[ALL]).toString());
                agentsign.setLine(1, (new StringBuilder("Frei:   ")).append(rooms[FREE]).toString());
                agentsign.setLine(2, (new StringBuilder("Belegt: ")).append(rooms[TAKEN]).toString());
                
                // freeRoom will contain the region name of a free room
                if(!freeRoom.isEmpty())
                    agentsign.setLine(3, freeRoom);
                else
                    agentsign.setLine(3, "No free room"); // auf deutsch passt es nicht aufs schild!
            }
        }
    }

    public int[] getRooms(String name, int low, int high)
    {
        freeRoom = "";
        
        // i[0] ist die anzahl aller zimmer
        // i[1] ist die anzahl belegter zimmer
        // i[2] ist die anzahl freier zimmer
        int i[] = new int[3];
        
        // HotelCount is just checking the rent.yml file of SimpleRegionMarket
        File ag = new File(HotelCount.agents);
        YamlConfiguration confighandle = YamlConfiguration.loadConfiguration(ag);
        
        for(Iterator<String> iterator = confighandle.getKeys(false).iterator(); iterator.hasNext();) {
            String world = iterator.next();
            ConfigurationSection path = confighandle.getConfigurationSection(world);
            
            for(Iterator<String> iterator1 = path.getKeys(false).iterator(); iterator1.hasNext();) {
                String region = iterator1.next();
                
                if(region.replaceAll("\\d*$", "").equalsIgnoreCase(name)) {
                    if((low  <= Integer.parseInt(region.replaceAll("[^\\d]", "")) &&
                    	high >= Integer.parseInt(region.replaceAll("[^\\d]", ""))) ||
                        high <= 0) {
                    	
                    	path = confighandle.getConfigurationSection(world).getConfigurationSection(region);
                        i[ALL]++; // it is actually a hotel room, so increase the counter
                        
                        if(!path.isConfigurationSection("signs")) {
                        	i[TAKEN]++; // if there is no sign it is considered taken
                        }
                        else if(path.getBoolean("taken") == true) {
                        	i[TAKEN]++;
                        }
                        else {
                        	i[FREE]++;
                            if(i[FREE] == 1)
                            	freeRoom = region;
                            else if(Integer.parseInt(region.replaceAll("[^0-9]", "")) < Integer.parseInt(freeRoom.replaceAll("[^0-9]", "")))
                            	freeRoom = region;
                        }
                    }
                }
            }
        }
        return i;
    }

    public SingleSign getSign(Location loc)
    {
        if(loc != null) {
            for(Iterator<SingleSign> iterator = allSigns.iterator(); iterator.hasNext();) {
                SingleSign obj = iterator.next();
                if(loc.equals(obj.getLocation()))
                    return obj;
            }
        }
        return null;
    }

    public boolean removeAgent(SingleSign agent)
    {
        if(agent != null) {
            agent.destroyAgent(false);
            allSigns.remove(agent);
            return true;
        }
        return false;
    }
    
    public String freeRoom = new String("");

    private ArrayList<SingleSign> allSigns;

    public static final int ALL = 0;
    public static final int FREE = 2;
    public static final int TAKEN = 1;
}
