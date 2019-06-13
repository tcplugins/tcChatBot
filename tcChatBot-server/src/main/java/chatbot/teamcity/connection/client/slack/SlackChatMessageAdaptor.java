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
	
	ObjectMapper mapper = new ObjectMapper();
	AtomicInteger messageSequenceId = new AtomicInteger(0);

	@Override
	public Request fromClient(JsonNode slackMessage, User user, MessageResponder responder) {
		Bundle b = new Bundle();
		b.put("channel", slackMessage.findValue("channel").asText());
		b.put("team", slackMessage.findValue("team").asText());
		b.put("client_msg_id", slackMessage.findValue("client_msg_id").asText());
		b.put("type", slackMessage.findValue("type").asText());
		b.put("raw_text", slackMessage.findValue("text").asText());
		b.put("user_name", user.getChatUser().getChatUserName());
		String messageWithoutKeyword = slackMessage.findValue("text").asText().substring(responder.getKeyword().length() + 1);
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
																			 s.toString())
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
				.replaceAll("\\{user\\}", "<@" + responseMessage.getBundle().get("user_name").toString() + ">")
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
			message.channel = bundle.get("channel").toString();
			message.type = "message";
			
			return message;
		}
	}

}
