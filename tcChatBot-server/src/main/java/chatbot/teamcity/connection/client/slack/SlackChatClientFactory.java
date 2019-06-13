package chatbot.teamcity.connection.client.slack;

import java.util.UUID;

import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.connection.ChatClientFactory;
import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.MessageReceiver;
import chatbot.teamcity.service.UserService;

public class SlackChatClientFactory implements ChatClientFactory {

	private final UserService userService;
	private final SlackUserService slackUserService;
	
	public SlackChatClientFactory(ChatClientManager chatClientManager, UserService userService, SlackUserService slackUserService) {
		this.userService = userService;
		chatClientManager.register(this);
		this.slackUserService= slackUserService;
	}
	
	@Override
	public ChatClient createChatClient(ChatClientConfig config, MessageReceiver messageReceiver) {
		if (config.getProperties().containsKey("token")) {
			SlackChatClient client = new SlackChatClient(
					config.getConfigId(),
					UUID.randomUUID().toString(),
					config.getProperties().get("token"),
					config.getProperties().get("keyword"),
					messageReceiver,
					this.userService,
					this.slackUserService,
					config.getEmailAutoMappingEnabled()
					);
			return client;
		}
		throw new ChatClientConfigurationException("No token found");
	}

	@Override
	public String getChatClientType() {
		return "slack";
	}


}
