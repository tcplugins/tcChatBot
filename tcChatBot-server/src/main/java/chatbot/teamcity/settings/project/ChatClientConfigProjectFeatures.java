package chatbot.teamcity.settings.project;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;

public interface ChatClientConfigProjectFeatures {

	@NotNull
	List<ChatClientConfigProjectFeature> getOwnFeatures(@NotNull final SProject project);

	SProjectFeatureDescriptor addFeature(@NotNull final SProject project,
			@NotNull final Map<String, String> featureParameters);

	void updateFeature(@NotNull final SProject project, @NotNull final String id,
			@NotNull final Map<String, String> featureParameters);

	@Nullable
	SProjectFeatureDescriptor removeFeature(@NotNull final SProject project, @NotNull final String id);
}