package chatbot.teamcity.service;

import chatbot.teamcity.model.Request;

public interface MessageReceiver {

	public void receive(Request request);
}
