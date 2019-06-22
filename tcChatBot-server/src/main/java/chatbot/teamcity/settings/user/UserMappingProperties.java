package chatbot.teamcity.settings.user;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.model.UserKey;
import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;

public class UserMappingProperties {
	
	private static final String PLUGIN_TYPE = "chatbot";
	private static final String PLUGIN_NAME = "tcChatBot";
	private static final String USER_MAPPING_PREFIX = "user.mapping.";
	private static final String USER_MAPPING_REASON_PREFIX = "reason.user.mapping.";

	private UserMappingProperties(){}
	
	private static final Pattern p = Pattern.compile("^.+\\:user\\.mapping\\.(.*)\\:(.*)\\:(.*)$");
	
	@NotNull
	public static PluginPropertyKey getMappingPropertyKey(@NotNull UserKey userKey) {
		return new PluginPropertyKey(PLUGIN_TYPE, PLUGIN_NAME, USER_MAPPING_PREFIX + userKey.getMappingKey());
	}
	
	@NotNull
	public static PluginPropertyKey getMappingReasonPropertyKey(@NotNull UserKey userKey) {
		return new PluginPropertyKey(PLUGIN_TYPE, PLUGIN_NAME, USER_MAPPING_REASON_PREFIX + userKey.getMappingKey());
	}
	
	@NotNull
	public static String getMappingPropertyKeySuffix() {
		return new PluginPropertyKey(PLUGIN_TYPE, PLUGIN_NAME, USER_MAPPING_PREFIX).getKey();
	}
	
	@NotNull
	public static String getMappingReasonPropertyKeySuffix() {
		return new PluginPropertyKey(PLUGIN_TYPE, PLUGIN_NAME, USER_MAPPING_REASON_PREFIX).getKey();
	}

	@Nullable
	public static UserKey getUserKey(PropertyKey propertyKey) {
		if (propertyKey != null 
				&& propertyKey instanceof PluginPropertyKey 
				&& ((PluginPropertyKey) propertyKey).getPluginName().equals(PLUGIN_NAME)) {
			Matcher matcher = p.matcher(propertyKey.getKey());
			if (matcher.matches()) {
				return new UserKey(matcher.group(1), matcher.group(2), matcher.group(3));
			}
		}
		return null;
	}
	
	@Nullable
	public static String getMappingReason(UserKey userKey, Map<PropertyKey, String> userProperties) {
			return userProperties.getOrDefault(getMappingReasonPropertyKey(userKey), "Unknown reason");
	}
}