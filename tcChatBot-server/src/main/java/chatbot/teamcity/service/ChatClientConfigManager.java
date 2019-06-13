package chatbot.teamcity.service;

import java.util.List;

import chatbot.teamcity.model.ChatClientConfig;

public interface ChatClientConfigManager {

		public List<ChatClientConfig> getAllConfigs();
		public ChatClientConfig getConfig(String configId);
		public void registerConfig(ChatClientConfig slackDevConfig); 
		
}
