package basic;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

// Referenced classes of package basic:
//            HotelCount, SignManager, SingleSign

public class config
{

    public config()
    {
        cfgSigns = new File((new StringBuilder(String.valueOf(HotelCount.signs))).append("signs.yml").toString());
    }

    public void load()
    {
        YamlConfiguration confighandle = YamlConfiguration.loadConfiguration(cfgSigns);
        for(Iterator<String> iterator = confighandle.getKeys(false).iterator(); iterator.hasNext();)
        {
            String world = iterator.next();
            ConfigurationSection path = confighandle.getConfigurationSection(world);
            for(Iterator<String> iterator1 = path.getKeys(false).iterator(); iterator1.hasNext();)
            {
                String signnr = iterator1.next();
                path = confighandle.getConfigurationSection(world).getConfigurationSection(signnr);
                if(path != null)
                {
                    String regionID = path.getString("RegionID");
                    int low = path.getInt("low");
                    int high = path.getInt("high");
                    World world_world = Bukkit.getWorld(world);
                    HotelCount.sman.addAgent(new Location(world_world, path.getDouble("X", 0.0D), path.getDouble("Y", 0.0D), path.getDouble("Z", 0.0D)), regionID, low, high);
                }
            }

        }

    }

    public void save()
    {
        YamlConfiguration confighandle = new YamlConfiguration();
        int i = 0;
        for(Iterator<SingleSign> iterator = HotelCount.getSM().getAllSigns().iterator(); iterator.hasNext();)
        {
            SingleSign obj = iterator.next();
            Location loc = obj.getLocation();
            String path = (new StringBuilder(String.valueOf(loc.getWorld().getName()))).append(".").append(Integer.toString(i)).toString();
            confighandle.set((new StringBuilder(String.valueOf(path))).append(".RegionID").toString(), obj.getRegionID());
            confighandle.set((new StringBuilder(String.valueOf(path))).append(".low").toString(), Integer.valueOf(obj.getLow()));
            confighandle.set((new StringBuilder(String.valueOf(path))).append(".high").toString(), Integer.valueOf(obj.getHigh()));
            confighandle.set((new StringBuilder(String.valueOf(path))).append(".X").toString(), Double.valueOf(loc.getX()));
            confighandle.set((new StringBuilder(String.valueOf(path))).append(".Y").toString(), Double.valueOf(loc.getY()));
            confighandle.set((new StringBuilder(String.valueOf(path))).append(".Z").toString(), Double.valueOf(loc.getZ()));
            i++;
        }

        try
        {
            confighandle.save(cfgSigns);
        }
        catch(IOException e)
        {
            Logger.getLogger("Minecraft").severe("-HotelCount: Could not save signs");
        }
    }

    private File cfgSigns;
}
