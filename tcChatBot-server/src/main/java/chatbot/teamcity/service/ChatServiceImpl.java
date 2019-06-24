package chatbot.teamcity.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.command.CommandNotFoundException;
import chatbot.teamcity.command.CommandResponse;
import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.exception.ChatClientExecutionException;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildServer;

@Service
public class ChatServiceImpl extends BuildServerAdapter implements MessageReceiver, ChatClientRestarter {
	
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
				chatClientConfigManager.setChatClientStatus(client.getConfigId(), "Started at " + LocalDateTime.now());
			} catch (ChatClientConfigurationException | ChatClientExecutionException ex) {
				Loggers.SERVER.warn("ChatServiceImpl :: Unable to create tcChatBot instance: " + ex.getMessage() );
				chatClientConfigManager.setChatClientStatus(config.getConfigId(), ex.getClass().getSimpleName() + " : " + ex.getMessage());
			}
		});
	}
	
	@Override
	public void serverShutdown() {
		this.chatClientManager.getAllChatClientInstances().forEach( client -> {
			try {
				client.stop();
				chatClientConfigManager.setChatClientStatus(client.getConfigId(), "Stopped at " + LocalDateTime.now());
			} catch (ChatClientExecutionException ex) {
				Loggers.SERVER.warn("ChatServiceImpl :: Unable to stop tcChatBot instance: " + ex.getMessage() );
				chatClientConfigManager.setChatClientStatus(client.getConfigId(), ex.getClass().getSimpleName() + " : " + ex.getMessage());
			}
		});
	}

	@Override
	public void restartChatClient(String configId) {
		stopChatClient(configId);
		startChatClient(configId);
	}

	@Override
	public void startChatClient(String configId) {
		ChatClientConfig config = this.chatClientConfigManager.getConfig(configId);
		try {
			ChatClient client = chatClientManager.createChatClientInstance(config, this);
			chatClientManager.register(client);
			client.start();
			chatClientConfigManager.setChatClientStatus(client.getConfigId(), "Started at " + LocalDateTime.now());
		} catch (ChatClientConfigurationException | ChatClientExecutionException ex) {
			Loggers.SERVER.warn("ChatServiceImpl :: Unable to create tcChatBot instance: " + ex.getMessage() );
			chatClientConfigManager.setChatClientStatus(config.getConfigId(), ex.getClass().getSimpleName() + " : " + ex.getMessage());
		}
		
	}

	@Override
	public void stopChatClient(String configId) {
		ChatClient client = chatClientManager.findChatInstanceForConfigId(configId);
		if (Objects.nonNull(client)) {
			try {
				client.stop();
				chatClientConfigManager.setChatClientStatus(client.getConfigId(), "Stopped at " + LocalDateTime.now());
			} catch (ChatClientExecutionException ex) {
				Loggers.SERVER.warn("ChatServiceImpl :: Unable to stop tcChatBot instance: " + ex.getMessage() );
				chatClientConfigManager.setChatClientStatus(client.getConfigId(), ex.getClass().getSimpleName() + " : " + ex.getMessage());
			}
		}
	}
	
}
