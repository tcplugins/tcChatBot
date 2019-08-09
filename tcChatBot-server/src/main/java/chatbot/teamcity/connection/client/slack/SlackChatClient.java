package chatbot.teamcity.connection.client.slack;

import com.fasterxml.jackson.databind.JsonNode;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.rtm.CloseListener;
import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;
import allbegray.slack.rtm.FailureListener;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import chatbot.teamcity.Loggers;
import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.exception.ChatClientExecutionException;
import chatbot.teamcity.exception.InvalidMessageRequestException;
import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.Bundle;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.service.MessageReceiver;
import chatbot.teamcity.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SlackChatClient implements ChatClient, EventListener, FailureListener, CloseListener {
	
	private static final String LOGGING_PREFIX = "SlackChatClient :: ";
	private static final String BUNDLE_KEY_USER_NAME = "user_name";
	private static final String BUNDLE_KEY_CHANNEL = "channel";
	
	@Getter
	private final String configId; 
	private final String chatInstanceId;
	private final String token;
	@Getter
	private final String keyword;
	private final MessageReceiver messageReceiver;
	private final UserService userService;
	private final SlackUserService slackUserService;
	@Getter
	private final boolean emailAutoMappingEnabled;
	
	private SlackRealTimeMessagingClient realTimeMessagingClient;
	private SlackChatMessageAdaptor slackChatMessasgeAdaptor = new SlackChatMessageAdaptor();
	
	@Override
	public String getChatClientType() {
		return "slack";
	}

	@Override
	public void respond(Response response) {
		try {
			realTimeMessagingClient.sendMessage(slackChatMessasgeAdaptor.toClient(response));
		} catch (Exception se) {
			Loggers.SERVER.warn(LOGGING_PREFIX + this.getInstanceId(), se);
			response.setMessage("Oh dear! An error occurred handling your request. Please see the `teamcity-server.log` for details. My ID is: '" + getInstanceId() + "' and the Exception was: " + se.getMessage());
			response.setMessenger(this);
		}
	}
	
	@Override
	public void onMessage(JsonNode message) {
		if (isRelevantEvent(message)) {
			try {
				User user = userService.findUser(getSlackUserKey(message));
				messageReceiver.receive(slackChatMessasgeAdaptor.fromClient(message, user, this));
			} catch (UserNotFoundException e) {
				UserKey userKey = getSlackUserKey(message);
				
				if (this.emailAutoMappingEnabled) {
					try {
						String emailAddress = this.slackUserService.getUserInfo(userKey.getChatUserName(), this.token)
								.getUser().getProfile().getEmail();
						User user = userService.autoMapUser(userKey, emailAddress);
						messageReceiver.receive(slackChatMessasgeAdaptor.fromClient(message, user, this));
					} catch (UserNotFoundException unf) {
						sendUserValidationUrl(message, userKey);
					}
				} else {
					sendUserValidationUrl(message, userKey);
				}
			} catch (Exception se) {
				Loggers.SERVER.warn(LOGGING_PREFIX + this.getInstanceId(), se);
				Response response = new Response();
				response.setBundle(new Bundle().set(BUNDLE_KEY_CHANNEL, getChannelName(message)).set(BUNDLE_KEY_USER_NAME, "unknown"));
				response.setMessage("Oh dear! An error occurred handling your request. Please see the `teamcity-server.log` for details. My ID is: '" + getInstanceId() + "' and the Exception was: " + se.getMessage());
				response.setMessenger(this);
				respond(response);
			}
		}
		
	}

	private void sendUserValidationUrl(JsonNode message, UserKey userKey) {
		String dmChannelName = this.slackUserService.getDmChannelName(userKey.getChatUserName(), this.token);
		Bundle bundle = new Bundle().set(BUNDLE_KEY_CHANNEL, getChannelName(message)).set(BUNDLE_KEY_USER_NAME, userKey.getChatUserName());
		Response setupResponse = new Response();
		setupResponse.setBundle(bundle);
		setupResponse.setMessage("Hi <@" + userKey.getChatUserName() + ">. We've not met yet. I'll DM you to get setup.");
		setupResponse.setMessenger(this);
		respond(setupResponse);
		User user = new User(userKey);
		Response dmResponse = new Response();
		bundle = new Bundle().set(BUNDLE_KEY_CHANNEL, dmChannelName).set(BUNDLE_KEY_USER_NAME, userKey.getChatUserName());
		dmResponse.setBundle(bundle);
		dmResponse.setMessenger(this);
		try {
			String validationUrl = userService.createValidationUrl(slackChatMessasgeAdaptor.fromClient(message, user, this));
			dmResponse.setMessage("Hi <@" + userKey.getChatUserName() + ">. I need to determine your account in TeamCity. "
					+ "Please click to <"+ validationUrl + "|link your Slack and TeamCity accounts> together.");
			respond(dmResponse);
		} catch (InvalidMessageRequestException ex) {
			dmResponse.setMessage("Hi <@" + userKey.getChatUserName() + ">. Sorry, an error occured trying to build a"
					+ "validation URL. Please try again later.");
			respond(dmResponse);
		}
	}

	private String getChannelName(JsonNode message) {
		Loggers.SERVER.info("Channel is "+ message.get(BUNDLE_KEY_CHANNEL) );
		return message.get(BUNDLE_KEY_CHANNEL).asText();
	}

	private boolean isRelevantEvent(JsonNode message) {
		return message.get("type").asText().equalsIgnoreCase("message") 
				&& message.get("text").asText().toLowerCase().startsWith(keyword + " ");
	}

	private UserKey getSlackUserKey(JsonNode message) {
		UserKey userKey = new UserKey(getChatClientType(), message.get("team").asText() , message.get("user").asText());
		Loggers.SERVER.info("User is " + userKey.getChatUserName());
		Loggers.SERVER.info("Team is " + userKey.getChatClientGroup());
		return userKey;
	}

	@Override
	public String getInstanceId() {
		return this.chatInstanceId;
	}

	@Override
	public Bundle createBundle(UserKey userKey) {
		return Bundle.create("team", userKey.getChatClientGroup())
					 .set("user", userKey.getChatUserName())
					 .set(BUNDLE_KEY_USER_NAME, userKey.getChatUserName())
					 .set(BUNDLE_KEY_CHANNEL, this.slackUserService.getDmChannelName(userKey.getChatUserName(), this.token));
	}



	@Override
	public void start() {
		try {
			Loggers.SERVER.info(LOGGING_PREFIX + "Starting chat instance '" + getInstanceInfo());
			realTimeMessagingClient = SlackClientFactory.createSlackRealTimeMessagingClient(token);
			realTimeMessagingClient.addListener(Event.MESSAGE, this);
			realTimeMessagingClient.addFailureListener(this);
			realTimeMessagingClient.addCloseListener(this);
			realTimeMessagingClient.connect();
		} catch (Exception ex) {
			Loggers.SERVER.info(LOGGING_PREFIX + "Exception occurred while starting chat instance '" + getInstanceInfo() + " :: " + ex.getMessage() );
			throw new ChatClientExecutionException(ex.getMessage());
		}
	}

	private String getInstanceInfo() {
		return this.getInstanceId()  + "' built from config '" + this.getConfigId()  + "'" ;
	}

	@Override
	public void stop() {
		try {
			Loggers.SERVER.info(LOGGING_PREFIX + "Stopping chat instance '" + getInstanceInfo() );
			realTimeMessagingClient.close();
		} catch (Exception ex) {
			Loggers.SERVER.info(LOGGING_PREFIX + "Exception occurred while stopping chat instance '" + getInstanceInfo() + " :: " + ex.getMessage() );
			throw new ChatClientExecutionException(ex.getMessage());
		}
	}

	@Override
	public void onFailure(Throwable t) {
		Loggers.SERVER.warn(LOGGING_PREFIX, t);
	}

	@Override
	@SuppressWarnings("squid:S2142")
	public void onClose() {
		Loggers.SERVER.warn("SlackChatClient :: Received closed connection event! Attempting to restart connection.");
		this.stop();
		try { Thread.sleep(10000); } catch (InterruptedException e) {
			Loggers.SERVER.debug(LOGGING_PREFIX + "Thread sleep interrupted");
		}
		this.start();
	}

}
