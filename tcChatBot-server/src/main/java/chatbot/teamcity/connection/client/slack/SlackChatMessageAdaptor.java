package chatbot.teamcity.connection.client.slack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.connection.ChatMessageAdapter;
import chatbot.teamcity.connection.client.ChatMessageAdaptorException;
import chatbot.teamcity.model.Bundle;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.service.MessageResponder;
import lombok.Getter;
import lombok.Setter;

public class SlackChatMessageAdaptor implements ChatMessageAdapter<JsonNode, String> {
	
	private static final String SLACK_KEY_TEXT = "text";
	private static final String BUNDLE_KEY_CLIENT_MSG_ID = "client_msg_id";
	private static final String BUNDLE_KEY_TYPE = "type";
	private static final String BUNDLE_KEY_RAW_TEXT = "raw_text";
	private static final String BUNDLE_KEY_USER_NAME = "user_name";
	private static final String BUNDLE_KEY_TEAM = "team";
	private static final String BUNDLE_KEY_CHANNEL = "channel";
	ObjectMapper mapper = new ObjectMapper();
	AtomicInteger messageSequenceId = new AtomicInteger(0);

	@Override
	public Request fromClient(JsonNode slackMessage, User user, MessageResponder responder) {
		Bundle b = new Bundle();
		b.put(BUNDLE_KEY_CHANNEL, slackMessage.findValue(BUNDLE_KEY_CHANNEL).asText());
		b.put(BUNDLE_KEY_TEAM, slackMessage.findValue(BUNDLE_KEY_TEAM).asText());
		b.put(BUNDLE_KEY_CLIENT_MSG_ID, slackMessage.findValue(BUNDLE_KEY_CLIENT_MSG_ID).asText());
		b.put(BUNDLE_KEY_TYPE, slackMessage.findValue(BUNDLE_KEY_TYPE).asText());
		b.put(BUNDLE_KEY_RAW_TEXT, slackMessage.findValue(SLACK_KEY_TEXT).asText());
		b.put(BUNDLE_KEY_USER_NAME, user.getChatUser().getChatUserName());
		String messageWithoutKeyword = slackMessage.findValue(SLACK_KEY_TEXT).asText().substring(responder.getKeyword().length() + 1);
		Loggers.SERVER.info(slackMessage.toString());
		return new Request(user, messageWithoutKeyword, responder, b);
		
	}

	@Override
	public String toClient(Response responseMessage) {
		try {
			return mapper.writeValueAsString(
					SlackMessage.build( responseMessage.getBundle(), 
										responseMessage.getMessages().stream()
																	 .map( s -> processTemplate(
																			 responseMessage, 
																			 s)
																		 )
																	 .collect( Collectors.joining("\n")), 
										messageSequenceId.incrementAndGet()
									)
					);
		} catch (JsonProcessingException e) {
			throw new ChatMessageAdaptorException(e);
		}
	}
	
	private String processTemplate(Response responseMessage, String string) {
		return string
				.replaceAll("\\{user\\}", "<@" + responseMessage.getBundle().get(BUNDLE_KEY_USER_NAME).toString() + ">")
				.replaceAll("\\{keyword\\}", responseMessage.getMessenger().getKeyword())
				.replaceAll("\\{command\\}", "`")
				.replaceAll("\\{/command\\}", "`")
				.replaceAll("\\{fixedWidth\\}", "`")
				.replaceAll("\\{/fixedWidth\\}", "`")
		;
	}

	@Getter @Setter
	public static class SlackMessage {
		int id;
		String type;
		String channel;
		String text;
		
		public static SlackMessage build(Bundle bundle, String text, int id) {
			
			SlackMessage message = new SlackMessage();
			message.id = id;
			message.text = text;
			message.channel = bundle.get(BUNDLE_KEY_CHANNEL).toString();
			message.type = "message";
			
			return message;
		}
	}

}
