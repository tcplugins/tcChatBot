package chatbot.teamcity.model;

import java.util.Map;

import jetbrains.buildServer.agent.Constants;
import lombok.Data;

@Data
public class ChatClientConfig {
	
	public static String BOOLEAN_PROPERTY_PREFIX = "boolean:";
	public static String SECURE_PROPERTY_PREFIX = Constants.SECURE_PROPERTY_PREFIX;

	String configId;
	/** Type, eg "slack" */
	String clientType;
	String projectInternalId;
	Boolean emailAutoMappingEnabled;
	Map<String,String> properties;
	
}
