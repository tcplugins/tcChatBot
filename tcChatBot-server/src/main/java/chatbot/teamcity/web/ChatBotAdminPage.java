package chatbot.teamcity.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.service.ChatClientConfigManager;
import chatbot.teamcity.service.UserMappingRepository;
import chatbot.teamcity.web.ChatBotConfigMapping.ChatBotConfigMappingBuilder;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;

public class ChatBotAdminPage extends AdminPage {
	public static final String TC_CHAT_BOT_ADMIN_ID = "tcChatBot";
	private final ChatClientManager myChatClientManager;
	private final ChatClientConfigManager myChatClientConfigManager;
	private final UserMappingRepository myUserMappingRepository;

	public ChatBotAdminPage(@NotNull PagePlaces pagePlaces, 
								  @NotNull PluginDescriptor descriptor,
								  @NotNull ChatClientManager chatClientManager,
								  @NotNull ChatClientConfigManager chatClientConfigManager,
								  @NotNull UserMappingRepository userMappingRepository
								  ) {
		super(pagePlaces);
		this.myChatClientManager = chatClientManager;
		this.myChatClientConfigManager = chatClientConfigManager;
		this.myUserMappingRepository = userMappingRepository;
		setPluginName(TC_CHAT_BOT_ADMIN_ID);
		setIncludeUrl(descriptor.getPluginResourcesPath("tcChatBot/adminTab.jsp"));
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
		
		List<ChatBotConfigMapping> configAndBots = new ArrayList<>();
		
		
		myChatClientConfigManager.getAllConfigs().forEach( config -> {
			ChatBotConfigMappingBuilder builder = ChatBotConfigMapping.builder().config(config);
			myChatClientManager.getAllChatClientInstances().forEach( bot -> {
				if (config.getConfigId().equals(bot.getConfigId())) {
					builder.client(bot);
				}
			});	
			configAndBots.add(builder.build());
		});
		
		model.put("chatBots", configAndBots);
		model.put("chatUsers", myUserMappingRepository.getAllUsersWithMappings());
	}
	
}