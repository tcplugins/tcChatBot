package chatbot.teamcity.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.MessageReceiver;

@Service
public class ChatClientManagerImpl implements ChatClientManager {
	
	Map<String,ChatClient> clients = new HashMap<>();
	Map<String,ChatClientFactory> factories = new HashMap<>();
	
	@Override
	public void register(ChatClient messenger) {
		this.clients.put(messenger.getInstanceId(), messenger);
	}
	
	@Override
	public ChatClient unregister(ChatClient messenger) {
		return this.clients.remove(messenger.getInstanceId());
	}
	
	@Override
	public void register(ChatClientFactory factory) {
		this.factories.put(factory.getChatClientType(), factory);
	}

	@Override
	public ChatClient getChatClientInstance(String id) {
		return this.clients.get(id);
	}
	
	@Override
	public List<ChatClient> getAllChatClientInstances() {
		return new ArrayList<>(this.clients.values());
	}

	@Override
	public ChatClient createChatClientInstance(ChatClientConfig config, MessageReceiver messageReceiver) {
		return this.factories.get(config.getClientType()).createChatClient(config, messageReceiver);
	}
	
	@Override
	public void shutdownAllClients() {
		for (ChatClient client : this.clients.values()) {
			client.stop();
		}
	}

	@Override
	public ChatClient findChatInstanceForConfigId(String chatClientConfigId) {
		for (ChatClient client : this.clients.values()) {
			if (client.getConfigId().equals(chatClientConfigId)) {
				return client;
			}
		}
		return null;
	}

	@Override
	public String getChatClientTypeName(String chatClientType) {
		if (Objects.nonNull(this.factories.get(chatClientType))) {
			return this.factories.get(chatClientType).getChatClientTypeName();
		}
		return chatClientType;
	}
}
