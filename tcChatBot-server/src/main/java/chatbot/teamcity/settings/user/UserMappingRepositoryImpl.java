package chatbot.teamcity.settings.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.service.UserMappingRepository;
import chatbot.teamcity.web.bean.ChatUserMappingBean;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;

/**
 *	A {@link UserMappingRepository} which stores its data in the 
 *	TeamCity user's properties. 
 */
public class UserMappingRepositoryImpl implements UserMappingRepository {

	private final UserModel userModel;
	private final ChatClientManager chatClientManager;

	public UserMappingRepositoryImpl(UserModel userModel, ChatClientManager chatClientManager) {
		this.userModel = userModel;
		this.chatClientManager = chatClientManager;
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
			
			// Find only the Properties that are for our plugin with the mapping prefix (not the reason prefix).
			Map<PropertyKey, String> ourProperties = sUser.getProperties().entrySet().stream()
											.filter(e -> e.getKey().getKey().contains(UserMappingProperties.getMappingPropertyKeySuffix()))
											.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
			if (! ourProperties.isEmpty()) {
				List<ChatUserMappingBean> beans = new ArrayList<>();
				ourProperties.forEach((p,s) -> {
					UserKey userKey = UserMappingProperties.getUserKey(p);
					// Use the mapping value to determine the reason prefix
					if (Objects.nonNull(userKey)) {
						String reason = UserMappingProperties.getMappingReason(userKey, ourProperties);
						beans.add(new ChatUserMappingBean(
								this.chatClientManager.getChatClientTypeName(userKey.getChatClientType()),
								userKey, 
								reason,
								toJson(userKey)));
					}
				});
				if (! beans.isEmpty()) {
					userProperties.put(sUser, beans);
				}
			}
		}
		return userProperties;
	}
	
	public static String toJson(UserKey userKey) {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
		return gson.toJson(userKey);
	}
}
