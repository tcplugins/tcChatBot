package chatbot.teamcity.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;

public class UserMappingProperties {
	
	private static final Pattern p = Pattern.compile("^.+\\:user\\.mapping\\.(.*)\\:(.*)\\:(.*)$");
	
	@NotNull
	public static PluginPropertyKey getMappingPropertyKey(@NotNull UserKey userKey) {
		return new PluginPropertyKey("chatbot", "tcChatBot", "user.mapping." + userKey.getMappingKey());
	}
	
	@NotNull
	public static PluginPropertyKey getMappingReasonPropertyKey(@NotNull UserKey userKey) {
		return new PluginPropertyKey("chatbot", "tcChatBot", "reason.user.mapping." + userKey.getMappingKey());
	}
	
	@NotNull
	public static String getMappingPropertyKeySuffix() {
		return new PluginPropertyKey("chatbot", "tcChatBot", "user.mapping.").getKey();
	}
	
	@NotNull
	public static String getMappingReasonPropertyKeySuffix() {
		return new PluginPropertyKey("chatbot", "tcChatBot", "reason.user.mapping.").getKey();
	}

	
	public static UserKey getUserKey(PropertyKey propertyKey) {
		if (propertyKey instanceof PluginPropertyKey && ((PluginPropertyKey) propertyKey).getPluginName().equals("tcChatBot")) {
			Matcher matcher = p.matcher(propertyKey.getKey());
			if (matcher.matches()) {
				return new UserKey(matcher.group(0), matcher.group(1), matcher.group(2));
			}
		}
		return null;
	}
}