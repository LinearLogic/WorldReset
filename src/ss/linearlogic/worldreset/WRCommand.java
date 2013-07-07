package ss.linearlogic.worldreset;
 
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WRCommand implements CommandExecutor {

	private final String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "WorldReset" + ChatColor.GRAY + "] ";
	private WorldReset plugin;

	public WRCommand(WorldReset plugin) {
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
					ChatColor.AQUA + "/wr reset [now/cancel]" + ChatColor.GRAY + " - schedules a world reset to " +
					"occur the next time the server is stopped and started. The 'now' flag stops the server; the " +
					"'cancel' flag removes a scheduled world reset.\n" + ChatColor.AQUA + "/wr settings [edit " +
					"<setting ID> <setting value>]" + ChatColor.GRAY + " - displays or edits the config settings");
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
					sender.sendMessage(prefix + ChatColor.GREEN + "Scheduled world reset! " + ChatColor.GRAY + "Stop " +
							"and start the server for the reset to occur.");
					plugin.logInfo("A world reset was scheduled by " + sender.getName() + ".");
					return true;
				}
				sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "a world reset is already " +
						"scheduled, and will occur when the server is stopped and started.");
				return true;
			}
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("now")) {
					plugin.logInfo(sender.getName() + " is stopping the server for a world reset.");
					plugin.getConfig().set("reset-worlds-on-next-restart", true);
					plugin.saveConfig();
					plugin.terminateForReset();
					return true;
				}
				if (args[1].equalsIgnoreCase("cancel")) {
					if (plugin.getConfig().getBoolean("reset-worlds-on-next-restart")) {
						plugin.getConfig().set("reset-worlds-on-next-restart", true);
						plugin.saveConfig();
						sender.sendMessage(prefix + ChatColor.GREEN + "Cancelled world reset!");
						plugin.logInfo("The scheduled world reset was cancelled by " + sender.getName() + ".");
						return true;
					}
					sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + " the worlds are not "
							+ "scheduled to be reset.");
					return true;
				}
			}
		}
		if (args[0].equalsIgnoreCase("settings")) {
			if (args.length == 1) {
				if (sender instanceof Player && !sender.hasPermission("worldreset.settings.view"))
					return msgNoPerms(sender);
				sender.sendMessage(ChatColor.WHITE + "<>" + ChatColor.GRAY + "-" + ChatColor.DARK_GRAY + "[" +
						ChatColor.DARK_GREEN + "WorldReset Settings" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY +
						"-" + ChatColor.WHITE + "<>\n" + ChatColor.GRAY + "[1] Reset scheduled: " + (plugin.getConfig()
						.getBoolean("reset-worlds-on-next-restart") ? ChatColor.GREEN + "yes" : ChatColor.RED + "no") +
						ChatColor.GRAY + "\n[2] Random seed: " + (plugin.getConfig().getBoolean("random-seed.enabled")
						? ChatColor.GREEN + "yes" : ChatColor.RED + "no") + ChatColor.GRAY + "\n[3] Shutdown for " +
						"reset when server is empty: " + (plugin.getConfig().getBoolean("reset-when-server-empty") ?
						ChatColor.GREEN + "yes" : ChatColor.RED + "no") + ChatColor.GRAY + "\n[4] Player count " +
						"needed to activate reset: " +	ChatColor.LIGHT_PURPLE + plugin.getConfig().getInt("player-" +
						"count-to-activate-reset") + ChatColor.GRAY + "\n[5] Always reset: " + (plugin.getConfig()
						.getBoolean("always-reset") ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
				return true;
			}
			if (args[1].equalsIgnoreCase("edit") && args.length == 4) {
				if (sender instanceof Player && !sender.hasPermission("worldreset.settings.edit"))
					return msgNoPerms(sender);
				int settingID;
				try {
					settingID = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					settingID = 0;
				}
				String path;
				switch (settingID) {
					case 1:
						path = "reset-worlds-on-next-restart";
						break;
					case 2:
						path = "random-seed.enabled";
						break;
					case 3:
						path = "reset-when-server-empty";
						break;
					case 4: // Player count needed to activate reset, the only integer setting
						int count;
						try {
							count = Integer.parseInt(args[3]);
						} catch (NumberFormatException e) {
							count = 0;
						}
						if (count < 1) {
							sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "the player count " +
									"value must be at least 1.");
							return true;
						}
						plugin.getConfig().set("player-count-to-activate-reset", count);
						plugin.saveConfig();
						sender.sendMessage(prefix + ChatColor.GREEN + "Settings updated!");
						return true;
					case 5:
						path = "always-reset";
						break;
					default: // Invalid setting ID
						sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "the setting ID " +
								"must be between 1 and 5, inclusive.");
						return true;
				}
				if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("yes"))
					plugin.getConfig().set(path, true);
				else if (args[3].equalsIgnoreCase("false") || args[3].equalsIgnoreCase("no"))
					plugin.getConfig().set(path, false);
				else {
					sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "setting requires a " +
							"boolean value ('true' or 'false').");
					return true;
				}
				plugin.saveConfig();
				sender.sendMessage(ChatColor.GREEN + "Settings updated!");
				return true;
			}
		}
		sender.sendMessage(prefix + ChatColor.RED + "Error: " + ChatColor.GRAY + "invalid command or arguments. " +
				"Type " + ChatColor.AQUA + "/wr help" + ChatColor.GRAY + " for assistance.");
		return true;
	}

	private boolean msgNoPerms(CommandSender sender) {
		sender.sendMessage(prefix + ChatColor.RED + "Uh oh! You don't have permission to do that...");
		return true;
	}
}
