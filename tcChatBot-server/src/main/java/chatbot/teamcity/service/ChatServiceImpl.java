package chatbot.teamcity.service;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Service;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.command.CommandNotFoundException;
import chatbot.teamcity.command.CommandResponse;
import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildServer;

@Service
public class ChatServiceImpl extends BuildServerAdapter implements MessageReceiver {
	
	private final SBuildServer sBuildServer;
	private final CommandService commandService;
	private final ChatClientManager chatClientManager;
	private final ChatClientConfigManager chatClientConfigManager;
	
	public ChatServiceImpl(
			SBuildServer sBuildServer,
			CommandService commandService,
			ChatClientManager chatClientManager,
			ChatClientConfigManager chatClientConfigManager
			) {
		this.sBuildServer = sBuildServer;
		this.commandService = commandService;
		this.chatClientManager= chatClientManager;
		this.chatClientConfigManager= chatClientConfigManager;
		this.sBuildServer.addListener(this);
	}

	@Override
	public void receive(Request request) {
		Loggers.SERVER.info("Handling request" + request.getMessage());
		try {
			CommandResponse response = this.commandService.findExecutorForCommand(request.getMessage()).handleRequest(new Context(), request);
			request.getMessenger().respond(response.getResponse());
		} catch (CommandNotFoundException ex) {
			Response errorResponse = new Response();
			errorResponse.setBundle(request.getBundle());
			errorResponse.setMessage("Sorry. I didn't understand your request. Send {command}" 
										+ request.getMessenger().getKeyword()
										+ " help{/command} for a list of commands.");
			errorResponse.setMessenger(request.getMessenger());
			request.getMessenger().respond(errorResponse);
		}
	}

	@Override
	public void serverStartup() {
		this.chatClientConfigManager.getAllConfigs().forEach( config -> {
			try {
				ChatClient client = this.chatClientManager.createChatClientInstance(config, this);
				chatClientManager.register(client);
				client.start();
			} catch (ChatClientConfigurationException ex) {
				Loggers.SERVER.warn("ChatServiceImpl :: Unable to create tcChatBot instance: " + ex.getMessage() );
			}
		});
	}
	
	@Override
	public void serverShutdown() {
		chatClientManager.shutdownAllClients();
	}
	
}
