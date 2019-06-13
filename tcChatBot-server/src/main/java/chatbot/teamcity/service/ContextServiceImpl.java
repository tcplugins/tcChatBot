package chatbot.teamcity.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import chatbot.teamcity.model.Context;

@Service
public class ContextServiceImpl implements ContextService {
	
	Map<String, Context> context = new HashMap<>();
	
	@Override
	public Context getContext(String userId, String channel) {
		// TODO Auto-generated method stub
		return null;
	}

}
