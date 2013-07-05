package ss.linearlogic.worldreset;
 
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetWorldCommand implements CommandExecutor {

	private String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "WorldReset" + ChatColor.GRAY + "] ";
	private WorldReset plugin;

	public ResetWorldCommand(WorldReset plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(prefix + "Running v" + plugin.getDescription().getVersion() + " by LinearLogic. Type " +
		ChatColor.AQUA + "/wr help" + ChatColor.GRAY + " for a list of commands.");
			return true;
		}
		if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			sender.sendMessage(ChatColor.WHITE + "<>" + ChatColor.GRAY + "-" + ChatColor.DARK_GRAY + "[" +
					ChatColor.DARK_GREEN + "WorldReset Commands" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "-" +
					ChatColor.WHITE + "<>\n" + ChatColor.AQUA + "/wr help" + ChatColor.GRAY + " - displays command " +
					"information\n" + ChatColor.AQUA + "/wr reload" + ChatColor.GRAY + " - reloads the config\n" +
					ChatColor.AQUA + "/wr reset [now]" + ChatColor.GRAY + " - schedules a world reset to occur the " +
					"next time the server is stopped and started. Appending the 'now' flag stops the server.");
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			if (sender instanceof Player && !sender.hasPermission("worldreset.reload"))
				return msgNoPerms(sender);
			plugin.reloadConfig();
			sender.sendMessage(prefix + ChatColor.GREEN + "Config reloaded!");
			return true;
		}
		if (args[0].equalsIgnoreCase("reset")) {
			if (sender instanceof Player && !sender.hasPermission("worldreset.reset"))
				return msgNoPerms(sender);
			if (args.length == 1) {
				if (!plugin.getConfig().getBoolean("reset-worlds-on-next-restart")) {
					plugin.getConfig().set("reset-worlds-on-next-restart", true);
					plugin.saveConfig();
					sender.sendMessage(prefix + ChatColor.GREEN + "Scheduled world reset. " + ChatColor.GRAY + "Stop " +
							"and start the server for the reset to occur.");
					plugin.logInfo("A world reset was scheduled by " + sender.getName() + ".");
					return true;
				}
				sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "A world reset is already " +
						"scheduled, and will occur when the server is stopped and started.");
				return true;
			}
			if (args.length == 2 && args[1].equalsIgnoreCase("now")) {
				plugin.logInfo(sender.getName() + " is stopping the server for a world reset.");
				plugin.getConfig().set("reset-worlds-on-next-restart", true);
				plugin.saveConfig();
				plugin.terminateForReset();
				return true;
			}
		}
		sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "Invalid command or arguments. " +
				"Type " + ChatColor.AQUA + "/wr help" + ChatColor.GRAY + " for assistance.");
		return true;
	}

	private boolean msgNoPerms(CommandSender sender) {
		sender.sendMessage(prefix + ChatColor.RED + "Uh oh! You don't have permission to do that...");
		return true;
	}
}
