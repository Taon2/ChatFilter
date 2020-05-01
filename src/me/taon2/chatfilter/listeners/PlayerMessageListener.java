package me.taon2.chatfilter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.taon2.chatfilter.ChatFilter;

public class PlayerMessageListener implements Listener {
	
	private ChatFilter plugin;
	private int count = 0;
	
	public PlayerMessageListener(ChatFilter plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().hasPermission("cf.bypass.filter")) { //filter bypass permission check
			String initialMessage = event.getMessage();
			String lowerCaseMessage = event.getMessage().toLowerCase();
			String finalMessage = event.getMessage();
			
			if (count == 0) {
				for (int i2 = 0; i2 < plugin.getCensoredWords().size(); i2++) { //checks string for censored words, and censors them into "*"
					if (lowerCaseMessage.contains(plugin.getCensoredWords().get(i2))) {
						String censored = "*";
						for (int i3 = 0; i3 < plugin.getCensoredWords().get(i2).length()-1; i3++) {
							censored += "*";
						}
						event.setCancelled(true);
						finalMessage = (finalMessage.replace(plugin.getCensoredWords().get(i2).toLowerCase(), censored));
					}
				}

				for (int i = 0; i < plugin.getNeverCensoredWords().size(); i++) { //checks censored string for words that are never censored, and adds them back into string
					String allowedWordSaid = plugin.getNeverCensoredWords().get(i);
					String temp = " ";
					int num = 0;
					int num2 = 0;
					if (lowerCaseMessage.contains(allowedWordSaid.toLowerCase())) {
						num = lowerCaseMessage.indexOf(allowedWordSaid.toLowerCase());
						num2 = (num + allowedWordSaid.length());
						finalMessage = (finalMessage.substring(0, num) + initialMessage.substring(num, num2) + finalMessage.substring(num2));
						for (int i4 = 0; i4 < allowedWordSaid.length()-1; i4++) {
							temp += " ";
						}
						lowerCaseMessage = (lowerCaseMessage.substring(0, num) + temp + lowerCaseMessage.substring(num2));
						count++;
						i--;
					}
				}
				
				if (finalMessage != initialMessage) { //returns freshly censored message to chat, if it is changed
					event.getPlayer().chat(finalMessage);
				}
			}
			else {
				count = 0;
			}
		}
	}
}

