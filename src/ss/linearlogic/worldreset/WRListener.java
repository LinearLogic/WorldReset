package ss.linearlogic.worldreset;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
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
	public void onClick(InventoryClickEvent e) {
		if (e.getRawSlot() != e.getView().convertSlot(e.getRawSlot())) { // The bottom inventory was clicked
			ClickType c = e.getClick();
			// shift-clicks are blocked. All other clicks are fine
			if (c.equals(ClickType.SHIFT_LEFT) || c.equals(ClickType.SHIFT_RIGHT)) {
				e.setCancelled(true);
				// tell the player he/she can't place items in that chest
				return;
			}
		} else { // This is trickier. You have to determine whether an item is being placed or picked up
			// shift-clicks are fine here, regardless
		}
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
