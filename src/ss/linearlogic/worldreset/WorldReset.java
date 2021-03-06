package ss.linearlogic.worldreset;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldReset extends JavaPlugin {

	private WRCommand cmd;
	private WRListener listener;
	private WRWorldManager wm;
	
	public void onLoad() {
		logInfo("Loading configuration...");
		saveDefaultConfig();

		logInfo("Loading world manager...");
		wm = new WRWorldManager(this);

		boolean backupsFound = false;
		File backupDir = new File(getDataFolder(), "backups");
		if (!backupDir.exists()) { //make sure there is a backup directory, and create one if there isn't
			logInfo("No backup directory found; creating one now.");
			logInfo("Place world folders you want to reset from in '.../plugins/WorldReset/backups'");
			backupDir.mkdirs();
		}
		if (!getConfig().getBoolean("reset-worlds-on-next-restart"))
			logInfo("No world resets scheduled in the config, aborting.");
		else {
			logInfo("Looking for world backups in '.../plugins/WorldReset/backups'");
			for (File backup : backupDir.listFiles()) //make sure there are valid world folders in the backup directory
				if ((backup.isDirectory()) && (backup.listFiles().length != 0))
					backupsFound = true;
			if (!backupsFound) {//backup folder doesn't contain world folders - cancel the import
				logSevere("A world reset is scheduled but there are no folders to backup from (ignore if ");
				logSevere("Aborting world transfer...");
			} else {
				wm.importWorlds(); // Do the stuff with the things
				logInfo("World transfer complete.");
			}
			if (getConfig().getBoolean("random-seed.enabled")) {
				logInfo("Loading worlds with randomly generated seeds...");
				wm.deleteWorlds();
			}
		}
		getConfig().set("reset-worlds-on-next-restart", getConfig().get("always-reset"));
		saveConfig();
	}

	public void onEnable() {
		logInfo("Registering listener...");
		listener = new WRListener(this);
		getServer().getPluginManager().registerEvents(listener, this);

		logInfo("Activating command handler...");
		cmd = new WRCommand(this);
		getCommand("worldreset").setExecutor(cmd);

		logInfo("Enabled!");
	}

	public void onDisable() {
		saveConfig();
		cmd = null;
		listener = null;
		wm = null;
		logInfo("Disabled!");
	}

	public void terminateForReset() {
		for (Player player : getServer().getOnlinePlayers())
			player.kickPlayer("Resetting the world(s)!");
		getServer().shutdown();
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
