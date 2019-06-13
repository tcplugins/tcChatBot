package chatbot.teamcity.connection;

import chatbot.teamcity.model.Bundle;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.service.MessageResponder;

public interface ChatClient extends MessageResponder {
	
	public void start();
	public void stop();

	public Bundle createBundle(UserKey userKey);
	public boolean isEmailAutoMappingEnabled();

}
