package chatbot.teamcity.settings.project;

import static chatbot.teamcity.settings.project.ChatClientConfigFactory.CONFIG_ID_KEY;

import jetbrains.buildServer.serverSide.ProjectsModelListener;
import jetbrains.buildServer.serverSide.ProjectsModelListenerAdapter;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.TeamCityCoreFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

public class ChatClientConfigStorage {

    private static final String PROJECT_FEATURE_TYPE = "tcChatBot";

    private final TeamCityCoreFacade teamCityCore;

    private Map<String, ChatClientConfig> myChatClientConfigCache;

    public ChatClientConfigStorage(@NotNull TeamCityCoreFacade teamCityCore,
                              	   @NotNull EventDispatcher<ProjectsModelListener> events) {
        this.teamCityCore = teamCityCore;
        events.addListener(new ProjectsModelListenerAdapter() {
            @Override
            public void projectFeatureAdded(@NotNull SProject project, @NotNull SProjectFeatureDescriptor projectFeature) {
                resetCache();
            }

            @Override
            public void projectFeatureRemoved(@NotNull SProject project, @NotNull SProjectFeatureDescriptor projectFeature) {
                resetCache();
            }

            @Override
            public void projectFeatureChanged(@NotNull SProject project, @NotNull SProjectFeatureDescriptor before, @NotNull SProjectFeatureDescriptor after) {
                resetCache();
            }
        });
    }

    public ChatClientConfig addChatClientConfig(@NotNull ChatClientConfig chatClientConfig) {
        Map<String, String> params = ChatClientConfigFactory.asMap(chatClientConfig);
        SProject sProject = teamCityCore.findProjectByIntId(chatClientConfig.getProjectInternalId());
        sProject.addFeature(PROJECT_FEATURE_TYPE, params);
        teamCityCore.persist(chatClientConfig.getProjectInternalId(), "ChatClientConfig added");
        Loggers.SERVER.info("ChatClientConfig " + chatClientConfig.getClientType() + " : " + chatClientConfig.getConfigId() + " is created in the project " + chatClientConfig.getProjectInternalId());
        getChatClientConfig(chatClientConfig.getConfigId());//populate cache
        return chatClientConfig;
    }

    @NotNull
    public List<ChatClientConfig> getChatClientConfigs(@NotNull SProject project) {
        return project.getOwnFeaturesOfType(PROJECT_FEATURE_TYPE)
        			  .stream()
        			  .map(this::fromProjectFeature)
        			  .collect(toList());
    }

    public ChatClientConfig removeChatClientConfig(@NotNull SProject project, @NotNull String configId) {
        Optional<SProjectFeatureDescriptor> featureDescriptor = project.getOwnFeaturesOfType(PROJECT_FEATURE_TYPE).stream()
                .filter(feature -> feature.getParameters().get(CONFIG_ID_KEY).equals(configId))
                .findFirst();

        if (featureDescriptor.isPresent()) {
            project.removeFeature(featureDescriptor.get().getId());
            teamCityCore.persist(project.getProjectId(), "ChatClientConfig removed");
            return fromProjectFeature(featureDescriptor.get());
        } else {
            return null;
        }
    }

    public boolean updateChatClientConfig(@NotNull ChatClientConfig config, @NotNull String description) {
        Optional<SProjectFeatureDescriptor> featureDescriptor = teamCityCore.findProjectByIntId(config.getProjectInternalId()).getOwnFeaturesOfType(PROJECT_FEATURE_TYPE).stream()
                .filter(feature -> feature.getParameters().get(CONFIG_ID_KEY).equals(config.getConfigId()))
                .findFirst();

        if (featureDescriptor.isPresent()) {
            Map<String, String> params = ChatClientConfigFactory.asMap(config);
            teamCityCore.findProjectByIntId(config.getProjectInternalId()).updateFeature(featureDescriptor.get().getId(), PROJECT_FEATURE_TYPE, params);
            teamCityCore.persist(config.getProjectInternalId(), description);
            return true;
        } else {
        	Loggers.SERVER.warn("ChatClientConfigStorage :: Unable to find existing config instance "
        			+ "of type '" + config.getClientType() + "' to update with ID: " + config.getConfigId());
            return false;
        }
    }

    @Nullable
    public ChatClientConfig getChatClientConfig(@NotNull String clientId) {
            if (myChatClientConfigCache == null) {
            	rebuildCache();
            }
            return myChatClientConfigCache.get(clientId);
    }
    
    private synchronized void rebuildCache() {
        myChatClientConfigCache = new HashMap<>();
        for (SProject project : teamCityCore.getActiveProjects()) {
            for (SProjectFeatureDescriptor feature : project.getOwnFeaturesOfType(PROJECT_FEATURE_TYPE)) {
                myChatClientConfigCache.put(feature.getParameters().get(ChatClientConfigFactory.CONFIG_ID_KEY), fromProjectFeature(feature));
            }
        }
        Loggers.SERVER.debug("ChatClientConfigStorage :: Rebuilt myChatClientConfigCache cache. It now contains " +  myChatClientConfigCache.size() + " entries.");
    }

    private synchronized void resetCache() {
        myChatClientConfigCache = null;
    }

    private ChatClientConfig fromProjectFeature(SProjectFeatureDescriptor feature) {
        return ChatClientConfigFactory.readFrom(feature.getParameters());
    }

	public Collection<ChatClientConfig> getAllChatClientConfigs() {
        if (myChatClientConfigCache == null) {
        	rebuildCache();
        }
        return this.myChatClientConfigCache.values();
	}
}