package chatbot.teamcity.service;

import chatbot.teamcity.model.Context;

public interface ContextService {
	
	public Context getContext(String userId, String channel);

}
