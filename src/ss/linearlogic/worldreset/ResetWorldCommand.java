package ss.linearlogic.worldreset;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetWorldCommand implements CommandExecutor {

	private WorldReset plugin;
	private int iteration = 1;

	public ResetWorldCommand(WorldReset plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("worldreset.reset")) {
			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "WorldReset" + ChatColor.GRAY + "] " + ChatColor.RED + "You don't have permission to schedule a world reset.");
			return true;
		}
		if (args.length > 0) {
			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "WorldReset" + ChatColor.GRAY + "] " + ChatColor.RED + "Usage:" + ChatColor.GRAY + "/resetworld");
			return true;
		}
		if (iteration == 1) {
			plugin.getConfig().set("reset-worlds-on-next-restart", true);
			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "WorldReset" + ChatColor.GRAY + "] " + ChatColor.GREEN + "The worlds will reset on next restart! Type '/resetworld' again to stop the server now!");
			plugin.saveConfig();
			
			iteration++;
			return true;
		}
		plugin.terminateForReset();
		return true;
	}
}
