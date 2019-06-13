package chatbot.teamcity.model;

import java.util.UUID;

import lombok.Data;

@Data
public class ValidationHolder {
	private final UUID uuid;
	private final UserKey userKey;
	private final String chatClientConfigId; 

}
