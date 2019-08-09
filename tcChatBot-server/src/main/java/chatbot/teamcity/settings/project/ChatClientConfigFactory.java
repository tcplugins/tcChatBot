package chatbot.teamcity.settings.project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chatbot.teamcity.model.ChatClientConfig;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;

public class ChatClientConfigFactory {
	
	public  static final String CONFIG_ID_KEY   = "configId";
	public  static final String NAME_KEY   		= "name";
	private static final String CLIENT_TYPE_KEY = "clientType";
	private static final String PROJECT_ID_KEY  = "projectInternalId";
	private static final String EMAIL_AUTO_KEY  = "boolean.emailAutoMappingEnabled";
	
	private static final List<String> KEYS = Arrays.asList(CONFIG_ID_KEY, NAME_KEY, CLIENT_TYPE_KEY, PROJECT_ID_KEY, EMAIL_AUTO_KEY);

	private ChatClientConfigFactory(){}
	
	public static ChatClientConfig readFrom(Map<String, String> parameters) {
		
		ChatClientConfig config = new ChatClientConfig();
		config.setConfigId(parameters.get(CONFIG_ID_KEY));
		config.setName(parameters.get(NAME_KEY));
		config.setProjectInternalId(parameters.get(PROJECT_ID_KEY));
		config.setClientType(parameters.get(CLIENT_TYPE_KEY));
		config.setEmailAutoMappingEnabled(Boolean.parseBoolean(parameters.get(EMAIL_AUTO_KEY)));
		config.setProperties(new HashMap<String, String>());
		
		parameters.forEach((k,v) -> {
			if ( ! KEYS.contains(k)) {
				config.getProperties().put(k, v);
			}
		});
		return config;
	}

	public static Map<String, String> asMap(ChatClientConfig config) {
		Map<String,String> properties = new HashMap<>();
		properties.putAll(config.getProperties());
		
		properties.put(CONFIG_ID_KEY, config.getConfigId());
		properties.put(NAME_KEY, config.getName());
		properties.put(PROJECT_ID_KEY, config.getProjectInternalId());
		properties.put(CLIENT_TYPE_KEY, config.getClientType());
		properties.put(EMAIL_AUTO_KEY, config.getEmailAutoMappingEnabled().toString());
		
		return properties;
	}

	public static ChatClientConfig fromDescriptor(SProjectFeatureDescriptor myDescriptor) {
		return readFrom(myDescriptor.getParameters());
	}
	
	public static String toJson(ChatClientConfig config) {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
		return gson.toJson(asMap(config));
	}

}
