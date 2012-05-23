package basic;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class SingleSign
{

    public SingleSign(Location location, String regionID, int low, int high)
    {
        this.low = 0;
        this.high = 0;
        this.location = location;
        world = location.getWorld().getName();
        this.regionID = regionID;
        this.low = low;
        this.high = high;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public String getWorld()
    {
        return world;
    }

    public void setWorld(String world)
    {
        this.world = world;
    }

    public String getRegionID()
    {
        return regionID;
    }

    public void setRegionID(String regionID)
    {
        this.regionID = regionID;
    }

    public int getLow()
    {
        return low;
    }

    public void setLow(int low)
    {
        this.low = low;
    }

    public int getHigh()
    {
        return high;
    }

    public void setHigh(int high)
    {
        this.high = high;
    }

    public boolean onWall()
    {
        return getLocation().getBlock().getType() == Material.WALL_SIGN;
    }

    public void destroyAgent(boolean drop)
    {
        getLocation().getBlock().setType(Material.AIR);
        if(drop)
            getLocation().getWorld().dropItem(getLocation(), new ItemStack(Material.SIGN, 1));
    }

    private Location location;
    private String world;
    private String regionID;
    private int low;
    private int high;
}
