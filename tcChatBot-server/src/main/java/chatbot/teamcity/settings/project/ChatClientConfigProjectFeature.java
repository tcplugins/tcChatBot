package chatbot.teamcity.settings.project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.model.ChatClientConfig;

public interface ChatClientConfigProjectFeature {

	@NotNull
	String getId();

	@Nullable
	ChatClientConfig getChatClientConfig();

}