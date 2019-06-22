package chatbot.teamcity.settings.project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.model.ChatClientConfig;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;

public class ChatClientConfigProjectFeatureImpl implements ChatClientConfigProjectFeature {

	@NotNull
	private final SProjectFeatureDescriptor myDescriptor;

	@Nullable
	private final ChatClientConfig myChatClientConfig;

	public ChatClientConfigProjectFeatureImpl(@NotNull final SProjectFeatureDescriptor descriptor) {
		myDescriptor = descriptor;
		myChatClientConfig = ChatClientConfigFactory.fromDescriptor(myDescriptor);
	}

	@Nullable
	@Override
	public ChatClientConfig getChatClientConfig() {
		return myChatClientConfig;
	}

	@NotNull
	@Override
	public String getId() {
		return myDescriptor.getId();
	}
}