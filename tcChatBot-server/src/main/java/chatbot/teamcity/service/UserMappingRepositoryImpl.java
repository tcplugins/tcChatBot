package chatbot.teamcity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.model.UserMappingProperties;
import chatbot.teamcity.web.bean.ChatUserMappingBean;
import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;

/**
 *	A {@link UserMappingRepository} which stores its data in the 
 *	TeamCity user's properties. 
 */
public class UserMappingRepositoryImpl implements UserMappingRepository {

	private final UserModel userModel;

	public UserMappingRepositoryImpl(UserModel userModel) {
		this.userModel = userModel;
	}
	
	@Override
	public SUser findUser(UserKey userKey) {
		SUser sUser = userModel.findUsersByPropertyValue(UserMappingProperties.getMappingPropertyKey(userKey), "", true)
						.getUsers().stream()
								   .findFirst().orElse(null);
		if (sUser != null) {
			Loggers.SERVER.debug("ChatBot UserMappingRepositoryImpl :: Found user mapping for user: '" 
					+ sUser.getUsername() + "' with mapping key: '" + UserMappingProperties.getMappingPropertyKey(userKey) 
					+ "'." );
		} else {
			Loggers.SERVER.debug("ChatBot UserMappingRepositoryImpl :: No user mapping found with mapping key: '" 
					+ UserMappingProperties.getMappingPropertyKey(userKey) + "'." );
		}
		return sUser;
	}

	@Override
	public void setMapping(SUser sUser, UserKey userKey, String reason) {
		sUser.setUserProperty(UserMappingProperties.getMappingPropertyKey(userKey), "");
		sUser.setUserProperty(UserMappingProperties.getMappingReasonPropertyKey(userKey), reason);
		Loggers.SERVER.info("ChatBot UserMappingRepositoryImpl :: Persisted user mapping for user: '" 
				+ sUser.getUsername() + "' with mapping key: '" + UserMappingProperties.getMappingPropertyKey(userKey) 
				+ "' and reason '" + reason + "'." );
	}

	@Override
	public void deleteMapping(SUser sUser, UserKey userKey) {
		sUser.deleteUserProperty(UserMappingProperties.getMappingPropertyKey(userKey));
		sUser.deleteUserProperty(UserMappingProperties.getMappingReasonPropertyKey(userKey));
		Loggers.SERVER.info("ChatBot UserMappingRepositoryImpl :: Removed user mapping for user: '" 
				+ sUser.getUsername() + "' with mapping key: '" + UserMappingProperties.getMappingPropertyKey(userKey) 
				+ "'." );
	}

	@Override
	public String getMappingReason(SUser sUser, UserKey userKey) {
		Loggers.SERVER.debug("ChatBot UserMappingRepositoryImpl :: Retreiving user mapping for user: '" 
				+ sUser.getUsername() + "' with mapping key: '" + UserMappingProperties.getMappingPropertyKey(userKey) 
				+ "'." );
		return sUser.getPropertyValue(UserMappingProperties.getMappingReasonPropertyKey(userKey));
	}

	@Override
	public Map<SUser, List<ChatUserMappingBean>> getAllUsersWithMappings() {
		Map<SUser, List<ChatUserMappingBean>> userProperties = new HashMap<>();
		for (SUser sUser : userModel.getAllUsers().getUsers()) {
			Map<PropertyKey, String> ourProperties = sUser.getProperties().entrySet().stream()
											.filter(e -> e.getKey().getKey().contains(UserMappingProperties.getMappingPropertyKeySuffix()))
											.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
			if (! ourProperties.isEmpty()) {
				List<ChatUserMappingBean> beans = new ArrayList<>();
				ourProperties.forEach((p,s) -> {
					PropertyKey key = new PluginPropertyKey(p.getKey().replace(UserMappingProperties.getMappingPropertyKeySuffix(), UserMappingProperties.getMappingReasonPropertyKeySuffix()));
					String reason = sUser.getPropertyValue(key);
					beans.add(new ChatUserMappingBean(UserMappingProperties.getUserKey(p), reason));
				});
				userProperties.put(sUser, beans);
			}
		}
		return userProperties;
	}
}