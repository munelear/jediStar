package fr.jedistar;

import static fr.jedistar.JediStarBotConstantes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import fr.jedistar.commands.RaidCommand;

public class JediStarBotMessageListener implements MessageCreateListener {

	Map<String,JediStarBotCommand> commandsMap;
	
	public JediStarBotMessageListener() {
		super();
		
		commandsMap = new HashMap<String,JediStarBotCommand>();
		
		commandsMap.put(RaidCommand.COMMANDE, new RaidCommand());
	}
	public void onMessageCreate(DiscordAPI api, Message messageRecu) {
		
		String messageAsString = messageRecu.getContent();
		
		//Si le message est vide ou ne commence pas par ! : Ne rien faire.
		if(messageAsString == null
				|| !messageAsString.startsWith(PREFIXE_COMMANDES)) {
			return;
		}
		
		//On retire le !
		messageAsString = messageAsString.substring(1);
		
		//On �clate les diff�rentes parties du message
		String[] messagePartsArray = messageAsString.split(" ");	
		
		if(messagePartsArray.length == 0) {
			return;
		}
		
		ArrayList<String> messageParts = new ArrayList<String>(Arrays.asList(messagePartsArray));
		messageParts.remove(0);
		
		String command = messagePartsArray[0];
		
		JediStarBotCommand botCommand = commandsMap.get(command);
		
		if(botCommand == null) {
			return;
		}
		
		String reponse = botCommand.repondre(messageParts);
		
		if(reponse == null || reponse == "") {
			return;
		}
		
		messageRecu.reply(reponse);
	}

}
