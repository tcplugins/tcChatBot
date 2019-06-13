package chatbot.teamcity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.model.ChatClientConfig;

public class ChatClientConfigManagerImpl implements ChatClientConfigManager {
	
	Map<String, ChatClientConfig> configurations = new HashMap<>();

	@Override
	public List<ChatClientConfig> getAllConfigs() {
		return new ArrayList<>(configurations.values());
	}

	@Override
	@Nullable
	public ChatClientConfig getConfig(String configId) {
		return configurations.get(configId);
	}

	@Override
	public void registerConfig(ChatClientConfig config) {
		configurations.put(config.getConfigId(), config);
	}

}
