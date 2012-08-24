package ss.linearlogic.worldreset;

import ss.linearlogic.worldreset.WRLogger;
import ss.linearlogic.worldreset.WRWorldManager;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldReset extends JavaPlugin {

	File worldFolder = Bukkit.getServer().getWorldContainer();
	public WRWorldManager wm = new WRWorldManager(this);
	public void onLoad()
	{
		WRLogger.logInfo("Looking for world backup in '.../plugins/WorldReset'");
		File backup = new File(getDataFolder(), "world");
		if (!backup.exists())
		{
			WRLogger.logSevere("Could not find a world folder to backup from!");
			WRLogger.logSevere("Aborting world reset...");
		}
		else 
		{
			WRLogger.logInfo("World backup found!");
			WRLogger.logInfo("Resetting world from backup...");
			for (File world : worldFolder.listFiles())
			{
				if (world.getName().equalsIgnoreCase("world") && world.isDirectory())
				{
					wm.delete(world);
					WRLogger.logInfo("Deletion Succeeded!");
				}
			}
			wm.importWorld();
			WRLogger.logInfo("Import succeeded!");
			WRLogger.logInfo("Reset complete.");
		}
	}
	public void onEnable()
	{
		getDataFolder().mkdirs();
		WRLogger.logInfo("Plugin successfully enabled!");
	}
	
	public void onDisable()
	{
		WRLogger.logInfo("Plugin successfully disabled!");
	}
}
