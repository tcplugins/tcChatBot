package chatbot.teamcity.connection;

import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.service.MessageResponder;

public interface ChatMessageAdapter<T, U> {

	public Request fromClient(T message, User user, MessageResponder responder);
	public U toClient(Response responseMessage);

}
