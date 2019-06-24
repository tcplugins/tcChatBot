package chatbot.teamcity.service;

import java.util.List;

import chatbot.teamcity.model.ChatClientConfig;
import jetbrains.buildServer.serverSide.SProject;

public interface ChatClientConfigManager {

		public List<ChatClientConfig> getAllConfigs();
		public ChatClientConfig getConfig(String configId);
		public void registerConfig(ChatClientConfig config);
		public List<ChatClientConfig> getConfigurationsForProject(SProject project);
		public void deleteConfig(SProject sProject, String configId);
		public void updateConfig(ChatClientConfig config, String description); 
		public String getChatClientStatus(String clientConfigId);
		public void setChatClientStatus(String clientConfigId, String message);
		
}
