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
        allSigns = new ArrayList();
        room = "";
    }

    public SingleSign addAgent(Location location, String regionID)
    {
        return addAgent(location, regionID, 0, 0);
    }

    public SingleSign addAgent(Location location, String regionID, int low, int high)
    {
        if(location != null)
        {
            SingleSign newSign = new SingleSign(location, regionID, low, high);
            getAllSigns().add(newSign);
            return newSign;
        } else
        {
            return null;
        }
    }

    public ArrayList getAllSigns()
    {
        return allSigns;
    }

    public void updateAgents()
    {
        Sign agentsign = null;
        
        for(Iterator iterator = allSigns.iterator(); iterator.hasNext(); agentsign.update())
        {
            SingleSign obj = (SingleSign)iterator.next();
            int rooms[] = getRooms(obj.getRegionID(), obj.getLow(), obj.getHigh());
            
            if ( obj.getLocation().getBlock().getState() instanceof Sign ) {
            	agentsign = (Sign)obj.getLocation().getBlock().getState();     
                
                agentsign.setLine(0, (new StringBuilder("Gesamt:")).append(rooms[0]).toString());
                agentsign.setLine(1, (new StringBuilder("Frei:")).append(rooms[2]).toString());
                agentsign.setLine(2, (new StringBuilder("Belegt:")).append(rooms[1]).toString());
                
                
                if(!room.equals(null))
                    agentsign.setLine(3, room);
                else
                    agentsign.setLine(3, "Kein freies Zimmer");
                room = "";
            }
            
            
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

    public SingleSign getSign(Location loc)
    {
        if(loc != null)
        {
            for(Iterator iterator = allSigns.iterator(); iterator.hasNext();)
            {
                SingleSign obj = (SingleSign)iterator.next();
                if(loc.equals(obj.getLocation()))
                    return obj;
            }

        }
        return null;
    }

    public boolean removeAgent(SingleSign agent)
    {
        boolean ret = false;
        if(agent != null)
        {
            agent.destroyAgent(false);
            allSigns.remove(agent);
            ret = true;
        }
        return ret;
    }

    private ArrayList allSigns;
    private String room;
}
