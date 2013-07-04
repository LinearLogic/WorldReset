package ss.linearlogic.worldreset;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WRListener implements Listener {

	private WorldReset plugin;
	private boolean maxReached = false;

	public WRListener(WorldReset wr) {
		this.plugin = wr;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.getConfig().getBoolean("reset-when-server-empty") && !maxReached &&
				plugin.getServer().getOnlinePlayers().length >= plugin.getConfig().getInt("player-count-to-activate-reset"))
			maxReached = true;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.getConfig().getBoolean("reset-when-server-empty") && maxReached && plugin.getServer().getOnlinePlayers().length <= 1)
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.terminateForReset();
				}
			}, 10L); //Half a second - plenty of time, just to play it safe
	}
}
