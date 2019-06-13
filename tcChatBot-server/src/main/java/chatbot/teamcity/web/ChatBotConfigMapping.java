package chatbot.teamcity.web;

import java.util.List;

import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.model.ChatClientConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class ChatBotConfigMapping {
	ChatClientConfig config;
	@Singular
	List<ChatClient> clients;
	
}