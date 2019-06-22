package chatbot.teamcity.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.ChatClientConfigManager;
import jetbrains.buildServer.controllers.admin.projects.EditProjectTab;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.AuthUtil;
import jetbrains.buildServer.serverSide.auth.SecurityContext;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class ChatBotProjectSettingsTab extends EditProjectTab {
	
	private static final String TAB_TITLE = "Chat Bots";
	private final SecurityContext mySecurityContext;
	private final SBuildServer myServer;
	private final ChatClientManager myChatClientManager;
	private final ChatClientConfigManager myChatClientConfigManager;

	
	public ChatBotProjectSettingsTab(
			@NotNull PagePlaces pagePlaces, 
			@NotNull PluginDescriptor descriptor,
			@NotNull ChatClientManager chatClientManager,
			@NotNull ChatClientConfigManager chatClientConfigManager,
			@NotNull PluginDescriptor pluginDescriptor,
			@NotNull SecurityContext securityContext,
			@NotNull SBuildServer sBuildServer) {
		super(pagePlaces, pluginDescriptor.getPluginName(), "tcChatBot/projectConfigTab.jsp", TAB_TITLE);
		this.myChatClientManager = chatClientManager;
		this.myChatClientConfigManager = chatClientConfigManager;
		this.mySecurityContext = securityContext;
		this.myServer = sBuildServer;
		addCssFile(pluginDescriptor.getPluginResourcesPath("tcChatBot/css/tcChatBot.css"));
		addJsFile(pluginDescriptor.getPluginResourcesPath("tcChatBot/projectConfigSettings.js"));
	}
	
	
    @NotNull
    @Override
    public String getTabTitle(@NotNull final HttpServletRequest request) {
        final SProject currentProject = getProject(request);
        if (currentProject == null) {
            return TAB_TITLE;
        }
        final List<ChatClientConfig> chatConfigs = myChatClientConfigManager.getConfigurationsForProject(currentProject);
        if (chatConfigs.isEmpty()) {
            return TAB_TITLE;
        }
        return TAB_TITLE + " (" + chatConfigs.size() + ")";
    }


    @Override
    public void fillModel(@NotNull final Map<String, Object> model, @NotNull final HttpServletRequest request) {
        final SProject currentProject = getProject(request);
        if (currentProject == null) {
            return;
        }
        final List<ChatClientConfig> chatConfigs = myChatClientConfigManager.getConfigurationsForProject(currentProject);
        model.put("chatConfigs", chatConfigs);
        model.put("sProject", currentProject);
        model.put("projectId", currentProject.getProjectId());
        model.put("projectExternalId", currentProject.getExternalId());
        model.put("userHasPermissionManagement", AuthUtil.hasPermissionToManageProject(mySecurityContext.getAuthorityHolder(), currentProject.getProjectId()));
    }
}
