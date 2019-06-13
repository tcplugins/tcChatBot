package chatbot.teamcity.connection;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.MessageReceiver;

public interface ChatClientFactory {
	
	public String getChatClientType();
	public ChatClient createChatClient(ChatClientConfig config,  MessageReceiver messageReceiver);

}
