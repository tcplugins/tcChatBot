package chatbot.teamcity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.settings.project.ChatClientConfigStorage;
import jetbrains.buildServer.serverSide.SProject;

public class ChatClientConfigManagerImpl implements ChatClientConfigManager {
	
	private ChatClientConfigStorage myChatClientConfigStorage;

	public ChatClientConfigManagerImpl(@NotNull ChatClientConfigStorage chatClientConfigStorage) {
		myChatClientConfigStorage = chatClientConfigStorage;
	}
	
	@Override
	public List<ChatClientConfig> getAllConfigs() {
		return new ArrayList<>(myChatClientConfigStorage.getAllChatClientConfigs());
	}

	@Override
	@Nullable
	public ChatClientConfig getConfig(String configId) {
		return myChatClientConfigStorage.getChatClientConfig(configId);
	}

	@Override
	public void registerConfig(ChatClientConfig config) {
		myChatClientConfigStorage.addChatClientConfig(config);
	}
	
	@Override
	public void deleteConfig(SProject sProject, String configId) {
		myChatClientConfigStorage.removeChatClientConfig(sProject, configId);
	}
	
	@Override
	public void updateConfig(ChatClientConfig config, String description) {
		myChatClientConfigStorage.updateChatClientConfig(config, description);
	}

	@Override
	public List<ChatClientConfig> getConfigurationsForProject(SProject project) {
		return myChatClientConfigStorage.getChatClientConfigs(project);
	}

}
