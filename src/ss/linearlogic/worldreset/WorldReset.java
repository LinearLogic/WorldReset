package ss.linearlogic.worldreset;

import ss.linearlogic.worldreset.WRWorldManager;
import ss.linearlogic.worldreset.util.WRConfig;
import ss.linearlogic.worldreset.util.WRLogger;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldReset extends JavaPlugin {

	private WRWorldManager wm = new WRWorldManager(this);
	private ResetWorldCommand rwc = new ResetWorldCommand(this);
	File worldFolder = Bukkit.getServer().getWorldContainer();
	public WRConfig configUtil = new WRConfig(this);
	
	public void onLoad()
	{
		WRLogger.logInfo("Loading config.yml...");
		configUtil.setupConfig();
		configUtil.loadConfig();
		boolean backupsFound = false;
		File backupDir = new File(getDataFolder(), "backups");
		if (!backupDir.exists()) { //make sure there is a backup directory, and create one if there isn't
			WRLogger.logInfo("No backup directory found; creating one now.");
			WRLogger.logInfo("Place world folders you want to reset from in '.../plugins/WorldReset/backups'");
			backupDir.mkdirs();
		}
		if (!configUtil.config.getBoolean("reset-worlds-on-next-restart")) {
			WRLogger.logInfo("No world resets scheduled in the config, aborting.");
			return;
		}
		for (File backup : backupDir.listFiles()) //make sure there are valid world folders in the backup directory
		{
			if ((backup.isDirectory()) && (backup.listFiles().length != 0)) {
				backupsFound = true;
			}
		}
		
		WRLogger.logInfo("Looking for world backups in '.../plugins/WorldReset/backups'");
		
		if (!backupsFound){ //backup folder doesn't contain world folders - cancel the import
			WRLogger.logSevere("Could not find any world folders to backup from!");
			WRLogger.logSevere("Aborting world reset...");
		} else {
			wm.importWorlds(); //pretty self explanatory
			
			WRLogger.logInfo("Reset complete!");
		}
		configUtil.config.set("reset-worlds-on-next-restart", false);
		configUtil.saveConfig();
		configUtil.loadConfig();
	}
	public void onEnable()
	{
		WRLogger.logInfo("Activating command handler...");
		getCommand("resetworld").setExecutor(rwc);
		WRLogger.logInfo("Plugin successfully enabled!");
	}
	
	public void onDisable()
	{
		configUtil.loadConfig();
		configUtil.saveConfig();
		WRLogger.logInfo("Plugin successfully disabled!");
	}
	
}
