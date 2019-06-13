package chatbot.teamcity.model;

import java.util.Map;

import lombok.Data;

@Data
public class ChatClientConfig {

	String configId;
	/** Type, eg "slack" */
	String clientType;
	Boolean emailAutoMappingEnabled;
	Map<String,String> properties;
}
