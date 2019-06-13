package chatbot.teamcity.model;

import java.util.HashMap;
import java.util.Map;

import chatbot.teamcity.connection.ChatClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class Context {
	
	String teamCityUserId;
	String channelId;
	ChatClient chatClient;
	Map<String, String> properties = new HashMap<>();

	public boolean has(String key) {
		return properties.containsKey(key);
	}

	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}
}
