package chatbot.teamcity.service;

import java.util.List;
import java.util.Map;

import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.web.bean.ChatUserMappingBean;
import jetbrains.buildServer.users.SUser;

public interface UserMappingRepository {
	
	public SUser findUser(UserKey userKey);
	public String getMappingReason(SUser sUser, UserKey userKey);
	public void setMapping(SUser sUser, UserKey userKey, String reason);
	public void deleteMapping(SUser sUser, UserKey userKey);
	public Map<SUser, List<ChatUserMappingBean>> getAllUsersWithMappings();

}
