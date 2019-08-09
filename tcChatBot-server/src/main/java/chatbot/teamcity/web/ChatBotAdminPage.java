package chatbot.teamcity.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.ChatClientConfigManager;
import chatbot.teamcity.service.UserMappingRepository;
import chatbot.teamcity.web.bean.ChatClientConfigWrapperBean;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ChatBotAdminPage extends AdminPage {
	public static final String TC_CHAT_BOT_ADMIN_ID = "tcChatBot";
	private final ChatClientConfigManager myChatClientConfigManager;
	private final UserMappingRepository myUserMappingRepository;
	private final ProjectManager myProjectManager;

	public ChatBotAdminPage(@NotNull PagePlaces pagePlaces, 
							@NotNull ProjectManager projectManager,
							@NotNull PluginDescriptor descriptor,
							@NotNull ChatClientConfigManager chatClientConfigManager,
							@NotNull UserMappingRepository userMappingRepository
						) 
	{
		super(pagePlaces);
		this.myChatClientConfigManager = chatClientConfigManager;
		this.myUserMappingRepository = userMappingRepository;
		this.myProjectManager = projectManager;
		setPluginName(TC_CHAT_BOT_ADMIN_ID);
		setIncludeUrl(descriptor.getPluginResourcesPath("tcChatBot/adminTab.jsp"));
		addCssFile(descriptor.getPluginResourcesPath("tcChatBot/css/tcChatBot.css"));
		addJsFile(descriptor.getPluginResourcesPath("tcChatBot/projectConfigSettings.js"));
		setTabTitle("Chat Bots");
		setPosition(PositionConstraint.after("clouds", "email", "jabber", "plugins", "tcDebRepository"));
		register();
	}

	@Override
	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
	}

	@NotNull
	public String getGroup() {
		return SERVER_RELATED_GROUP;
	}
	
	@Override
	public void fillModel(Map<String, Object> model, HttpServletRequest request) {
		Map<SProject, List<ChatClientConfigWrapperBean>> projectConfigsAndBots = new LinkedHashMap<>();
		
		myProjectManager.getActiveProjects().forEach(project -> {
			List<ChatClientConfig> configs = myChatClientConfigManager.getConfigurationsForProject(project);
			if (!configs.isEmpty()) {
				projectConfigsAndBots.put(project, getConfigs(configs));
			}
		});
		model.put("chatBots", projectConfigsAndBots);
        model.put("userHasPermissionManagement", true);
        model.put("chatUsers", myUserMappingRepository.getAllUsersWithMappings());
    }
    
    private List<ChatClientConfigWrapperBean> getConfigs(List<ChatClientConfig> configurationsForProject) {
    	return configurationsForProject.stream()
    								   .map(c -> new ChatClientConfigWrapperBean(
    										   			c, 
    										   			this.myChatClientConfigManager.getChatClientStatus(c.getConfigId())))
    								   .collect(Collectors.toList());
	}
	
}