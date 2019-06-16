package chatbot.teamcity.settings;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import chatbot.teamcity.model.UserKey;
import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;

public class UserMappingPropertiesTest {

	@Test
	public void testGetMappingPropertyKey() {
		assertEquals("plugin:chatbot:tcChatBot:user.mapping.slack:ABC12345:myuserName", 
					 UserMappingProperties.getMappingPropertyKey(
						new UserKey("slack", "ABC12345", "myuserName")).getKey());
	}

	@Test
	public void testGetMappingReasonPropertyKey() {
		assertEquals("plugin:chatbot:tcChatBot:reason.user.mapping.slack:ABC12345:myuserName", 
				 UserMappingProperties.getMappingReasonPropertyKey(
					new UserKey("slack", "ABC12345", "myuserName")).getKey());
	}

	@Test
	public void testGetMappingPropertyKeySuffix() {
		assertEquals("plugin:chatbot:tcChatBot:user.mapping.", 
				 UserMappingProperties.getMappingPropertyKeySuffix());
	}

	@Test
	public void testGetMappingReasonPropertyKeySuffix() {
		assertEquals("plugin:chatbot:tcChatBot:reason.user.mapping.", 
				 UserMappingProperties.getMappingReasonPropertyKeySuffix());
	}

	@Test
	public void testGetUserKey() {
		PluginPropertyKey pluginPropertyKey = new PluginPropertyKey("plugin:chatbot:tcChatBot:user.mapping.slack:ABC12345:myuserName");
		UserKey userKey = UserMappingProperties.getUserKey(pluginPropertyKey);
		assertEquals("ABC12345", userKey.getChatClientGroup());
		assertEquals("slack", userKey.getChatClientType());
		assertEquals("myuserName", userKey.getChatUserName());
	}

	@Test
	public void testGetMappingReason() {
		Map<PropertyKey, String> userProperties = new HashMap<>();

		UserKey userKey = new UserKey("slack", "ABC12345", "myuserName");
		userProperties.put(UserMappingProperties.getMappingPropertyKey(userKey), null);
		userProperties.put(UserMappingProperties.getMappingReasonPropertyKey(userKey), "Some reason");
		
		UserKey userKey2 = new UserKey("slack", "123ABC", "myuserName");
		userProperties.put(UserMappingProperties.getMappingPropertyKey(userKey2), null);
		userProperties.put(UserMappingProperties.getMappingReasonPropertyKey(userKey2), "Some other reason");

		assertEquals("Some reason", UserMappingProperties.getMappingReason(userKey, userProperties));
		assertEquals("Some other reason", UserMappingProperties.getMappingReason(userKey2, userProperties));
	}

}
