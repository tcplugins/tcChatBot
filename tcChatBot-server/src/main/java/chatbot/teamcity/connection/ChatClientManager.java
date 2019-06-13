package chatbot.teamcity.connection;

import java.util.List;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.MessageReceiver;

public interface ChatClientManager {

	public void register(ChatClient client);
	public ChatClient unregister(ChatClient messenger);
	public void register(ChatClientFactory factory);
	public ChatClient getChatClientInstance(String id);
	public List<ChatClient> getAllChatClientInstances();
	public ChatClient createChatClientInstance(ChatClientConfig config, MessageReceiver messageReceiver);
	public void shutdownAllClients();
	public ChatClient findChatInstanceForConfigId(String chatClientConfigId);
	
}