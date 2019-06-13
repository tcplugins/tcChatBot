package chatbot.teamcity.web;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.ValidationHolder;
import chatbot.teamcity.service.UserService;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.SecurityContext;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;

public class ChatBotUserLinkingController extends BaseController {
	
	private PluginDescriptor myPluginDescriptor;
	private UserService myUserService;
	private final ChatClientManager myChatClientManager;

	public ChatBotUserLinkingController(@NotNull final SBuildServer buildServer,
			@NotNull final UserService userService, @NotNull ChatClientManager chatClientManager,
			@NotNull final SecurityContext securityContext,
			@NotNull final PluginDescriptor pluginDescriptor, @NotNull final WebControllerManager manager) {
		super(buildServer);
		myPluginDescriptor = pluginDescriptor;
		myUserService = userService;
		myChatClientManager = chatClientManager;
		manager.registerController("/chatbot/linkUser.html", this);
	}

	@Override
	protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final String uuid = request.getParameter("token");
		final SUser user = SessionUser.getUser(request);
		if (uuid != null) {
			ValidationHolder holder = myUserService.validateUser(UUID.fromString(uuid), user);
			ChatClient client = myChatClientManager.findChatInstanceForConfigId(holder.getChatClientConfigId());
			if (client != null) {
				Response message = new Response();
				message.setBundle(client.createBundle(holder.getUserKey()));
				message.setMessenger(client);
				message.setMessage("Thanks. Your teamcity account has been linked.");
				client.respond(message);
			}
		}
		return null;
	}

}
