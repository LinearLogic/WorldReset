package ss.linearlogic.worldreset;

import ss.linearlogic.worldreset.WRWorldManager;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldReset extends JavaPlugin {

	private WRWorldManager wm = new WRWorldManager(this);
	private WRConfig configUtil = new WRConfig(this);
	private WRListener listener = new WRListener(this);
	private ResetWorldCommand rwc = new ResetWorldCommand(this);
	
	public void onLoad() {
		logInfo("Loading config.yml...");
		configUtil.setupConfig();
		configUtil.loadConfig();
		
		boolean backupsFound = false;
		File backupDir = new File(getDataFolder(), "backups");
		if (!backupDir.exists()) { //make sure there is a backup directory, and create one if there isn't
			logInfo("No backup directory found; creating one now.");
			logInfo("Place world folders you want to reset from in '.../plugins/WorldReset/backups'");
			backupDir.mkdirs();
		}
		if (!getConfig().getBoolean("reset-worlds-on-next-restart")) {
			logInfo("No world resets scheduled in the config, aborting.");
			return;
		}
		if (getConfig().getBoolean("random-seed")) {
			logInfo("Loading worlds with randomly generated seeds...");
			wm.deleteWorlds();
			return;
		}
		
		logInfo("Looking for world backups in '.../plugins/WorldReset/backups'");
		for (File backup : backupDir.listFiles()) { //make sure there are valid world folders in the backup directory
			if ((backup.isDirectory()) && (backup.listFiles().length != 0)) {
				backupsFound = true;
			}
		}
		if (!backupsFound) { //backup folder doesn't contain world folders - cancel the import
			logSevere("Could not find any world folders to backup from!");
			logSevere("Aborting world reset...");
		} else {
			wm.importWorlds(); //pretty self explanatory
			
			logInfo("Reset complete!");
		}
		getConfig().set("reset-worlds-on-next-restart", getConfig().get("always-reset"));
		configUtil.saveConfig();
		configUtil.loadConfig();
	}
	public void onEnable() {
		logInfo("Registering listener...");
		getServer().getPluginManager().registerEvents(listener, this);
		logInfo("Activating command handler...");
		getCommand("resetworld").setExecutor(rwc);
		logInfo("Plugin successfully enabled!");
	}
	
	public void onDisable() {
		configUtil.loadConfig();
		configUtil.saveConfig();
		logInfo("Plugin successfully disabled!");
	}
	
	public void terminateForReset() {
		for (Player player : getServer().getOnlinePlayers())
			player.kickPlayer("Resetting the world(s)!");
		getServer().shutdown();
	}
	
	public WRConfig getConfigUtil() {
		return this.configUtil;
	}
	
	public void logInfo(String message) {
		getLogger().info(message);
	}
	
	public void logWarning(String message) {
		getLogger().warning(message);
	}
	
	public void logSevere(String message) {
		getLogger().severe(message);
	}
}
