package chatbot.teamcity.service;

import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.model.Bundle;
import chatbot.teamcity.model.User;

public interface ChatService {
	
	/** Handle a message from the ChatClient, and pass it to the chat server. */ 
	public void handleMessage(User user, String message, ChatClient client, Bundle bundle);

}
