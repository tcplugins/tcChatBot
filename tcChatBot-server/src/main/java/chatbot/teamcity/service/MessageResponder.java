package chatbot.teamcity.service;

import chatbot.teamcity.model.Response;

/** 
 * Responds to messages from the Server, and sends them to the client
 */
public interface MessageResponder {
	public String getChatClientType();
	public String getInstanceId();
	public String getConfigId();
	public String getKeyword(); 
	public void respond(Response response);
}
