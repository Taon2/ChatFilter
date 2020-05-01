package me.taon2.chatfilter.commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.taon2.chatfilter.ChatFilter;
import net.md_5.bungee.api.ChatColor;

public class ChatFilterCommand implements CommandExecutor {

	private ChatFilter plugin;
	
	public ChatFilterCommand(ChatFilter plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) { //checks required parameters 
			sender.sendMessage(ChatColor.RED + "Usage: /cf <add/remove/list> <censor/allowed> <word> <word> <word> ...");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("cf")) {
			
			if (sender.hasPermission("cf.add")) { //add permission check
				if (args[0].equalsIgnoreCase("add")) { //checks first parameter to be add
					if (args.length < 3) { //checks required parameters for add
						sender.sendMessage(ChatColor.RED + "Usage: /cf <add> <censor/allowed> <word> <word> <word> ...");
						return true;
					}
					if (args[1].equalsIgnoreCase("censor")) { //checks second parameter to be censor list
						for (int i = 2; i < args.length; i++) {
							String word = args[i].toLowerCase();
							if (!plugin.getCensoredWords().contains(word)){
								Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
									try {
										plugin.getStatement().executeUpdate("INSERT INTO `censoredwordstable` (`censoredwords`) VALUES ('" + word + "');");
									} catch (SQLException e) {
										e.printStackTrace();
									}
								});
								plugin.addCensoredWord(word);
								sender.sendMessage(ChatColor.GOLD + word + " will now be filtered.");
							}
							else {
								sender.sendMessage(ChatColor.RED + word + " is already filtered.");
							}
						}
					}
					if (args[1].equalsIgnoreCase("allowed")) { //checks second parameter to be allowed list
						for (int i = 2; i < args.length; i++) {
							String word = args[i].toLowerCase();
							if (!plugin.getNeverCensoredWords().contains(word)){
								Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
									try {
										plugin.getStatement().executeUpdate("INSERT INTO `nevercensoredwordstable` (`nevercensoredwords`) VALUES ('" + word + "');");
									} catch (SQLException e) {
										e.printStackTrace();
									}
								});
								plugin.addNeverCensoredWord(word);
								sender.sendMessage(ChatColor.GOLD + word + " will now never be filtered.");
							}
							else {
								sender.sendMessage(ChatColor.RED + word + " is already never filtered.");
							}
						}
					}
				}
			}
			else if (!sender.hasPermission("cf.add") && (args[0].equalsIgnoreCase("add"))) { //reply if no permission
				sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
			}
			
			if (sender.hasPermission("cf.remove")) { //remove permission check
				if (args[0].equalsIgnoreCase("remove")) { //checks first parameter to be remove
					if (args.length < 3) { //checks required parameters for remove
						sender.sendMessage(ChatColor.RED + "Usage: /cf <remove> <censor/allowed> <word> <word> <word> ...");
						return true;
					}
					if (args[1].equalsIgnoreCase("censor")) { //checks second parameter to be censor list
						for (int i = 2; i < args.length; i++) {
							String word = args[i].toLowerCase();
							if (plugin.getCensoredWords().contains(word)) {
								Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
									try {
										plugin.getStatement().executeUpdate("DELETE FROM `censoredwordstable` WHERE `censoredwords` = ('" + word + "');");
									} catch (SQLException e) {
										e.printStackTrace();
									}
								});
								plugin.removeCensoredWord(word);
								sender.sendMessage(ChatColor.GOLD + word + " will now be unfiltered.");
							}
							else {
								sender.sendMessage(ChatColor.RED + word + " is not filtered.");
							}
						}
					}
					if (args[1].equalsIgnoreCase("allowed")) { //checks second parameter to be allowed list
						for (int i = 2; i < args.length; i++) {
							String word = args[i].toLowerCase();
							if (plugin.getNeverCensoredWords().contains(word)){
								Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
									try {
										plugin.getStatement().executeUpdate("DELETE FROM `nevercensoredwordstable` WHERE `nevercensoredwords` = ('" + word + "');");
									} catch (SQLException e) {
										e.printStackTrace();
									}
								});
								plugin.removeNeverCensoredWord(word);
								sender.sendMessage(ChatColor.GOLD + word + " can now be filtered.");
							}
							else {
								sender.sendMessage(ChatColor.RED + word + " can already be filtered.");
							}
						}
					}
				}
			}
			else if (!sender.hasPermission("cf.remove") && (args[0].equalsIgnoreCase("remove"))) { //reply if no permission
				sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
			}
			
			if(sender.hasPermission("cf.list")) { //list permission check
				if (args[0].equalsIgnoreCase("list")) { //checks first parameter to be list
					if (args.length < 2) { //checks required parameters for list
						sender.sendMessage(ChatColor.RED + "Usage: /cf list <censor/allowed>");
						return true;
					}
					if (args[1].equalsIgnoreCase("censor")) { //checks second parameter to be censor list
						if (plugin.getCensoredWords().size() == 0) {
							sender.sendMessage(ChatColor.GOLD + "Filtered words: " + ChatColor.WHITE + "[" + ChatColor.ITALIC + "None" + ChatColor.RESET + "]");
						}
						else {
							sender.sendMessage(ChatColor.GOLD + "Filtered words: " + ChatColor.WHITE + plugin.getCensoredWords().toString());
						}
					}//censor
					if (args[1].equalsIgnoreCase("allowed")) { //checks second parameter to be allowed list
						if (plugin.getNeverCensoredWords().size() == 0) {
							sender.sendMessage(ChatColor.GOLD + "Never filtered words: " + ChatColor.WHITE + "[" + ChatColor.ITALIC + "None" + ChatColor.RESET + "]");
						}
						else {
							sender.sendMessage(ChatColor.GOLD + "Never filtered words: " + ChatColor.WHITE + plugin.getNeverCensoredWords().toString());
						}
					}
				}
			}
			else if (!sender.hasPermission("cf.list") && (args[0].equalsIgnoreCase("list"))) { //reply if no permission
				sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
			}
			
			if(sender.hasPermission("cf.help")) { //help permission check
				if (args[0].equalsIgnoreCase("help")) { //checks first parameter to be help
					sender.sendMessage(ChatColor.GOLD + "----- ChatFilter Commands -----");
					sender.sendMessage(ChatColor.GOLD + "/cf <add/remove> <censor/allowed> <word> <word> <word> ...");
					sender.sendMessage(ChatColor.GOLD + "/cf <list> <censor/allowed>");
				}
			}
			else if (!sender.hasPermission("cf.help") && (args[0].equalsIgnoreCase("help"))) { //reply if no permission
				sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "Usage: /cf <add/remove/list> <censor/allowed> <word> <word> <word> ...");
		}
		return true;
	}
}
