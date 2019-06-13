package chatbot.teamcity.connection.client.slack;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.connection.client.slack.model.ImOpenResponse;
import chatbot.teamcity.connection.client.slack.model.SlackUserInfo;

@Service
public class SlackUserService {
	
    private RestTemplate restTemplate;
    private Map<String,String> userToChannelMapping = new HashMap<>();
    
    public SlackUserService(
    		RestTemplate restTemplate
    		)
    {
    	this.restTemplate = restTemplate;
    }
    
    private static final String SLACK_API_URL_PREFIX = "https://slack.com/api";
    private static final String USERS_INFO_URL_SUFFIX = "/users.info";   
    private static final String USERS_DM_URL_SUFFIX = "/im.open";
    
    public SlackUserInfo getUserInfo(String userId, String slackToken) {
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> postVars= new LinkedMultiValueMap<>();
    	postVars.add("token", slackToken);
    	postVars.add("user", userId);
    	
    	Loggers.SERVER.debug("requesting user info for " + userId);
    	
    	HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(postVars, headers);
    	SlackUserInfo userInfo = restTemplate.postForEntity(SLACK_API_URL_PREFIX + USERS_INFO_URL_SUFFIX, entity, SlackUserInfo.class).getBody();
    	
    	Loggers.SERVER.debug("user info: " + userInfo);
    	return userInfo;
    }
	

    public String getDmChannelName(String userId, String slackToken) {
    	
    	if (! userToChannelMapping.containsKey(userId)) {
    		
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> postVars= new LinkedMultiValueMap<>();
        	postVars.add("token", slackToken);
        	postVars.add("user", userId);
        	
        	Loggers.SERVER.debug("requesting user info for " + userId);
        	
        	HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(postVars, headers);
        	ImOpenResponse userDmInfo = restTemplate.postForEntity(SLACK_API_URL_PREFIX + USERS_DM_URL_SUFFIX, entity, ImOpenResponse.class).getBody();
        	
        	if (userDmInfo.getOk() && !userDmInfo.getChannel().getId().isEmpty()) {
        		this.userToChannelMapping.put(userId, userDmInfo.getChannel().getId());
        	}
        	
        	Loggers.SERVER.debug("dm info: " + userDmInfo);
    	}
        	
    	return this.userToChannelMapping.get(userId);
        	
    }
}