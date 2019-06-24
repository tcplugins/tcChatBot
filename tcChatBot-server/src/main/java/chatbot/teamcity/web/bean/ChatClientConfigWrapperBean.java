package chatbot.teamcity.web.bean;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.settings.project.ChatClientConfigFactory;
import lombok.Getter;

@Getter
public class ChatClientConfigWrapperBean {
	
	final ChatClientConfig config;
	final String json;
	final String status;
	
	public ChatClientConfigWrapperBean(ChatClientConfig chatClientConfig, String status) {
		this.config = chatClientConfig;
		this.json = ChatClientConfigFactory.toJson(chatClientConfig).replaceAll("secure:", "secure_").replaceAll("boolean.", "boolean_");
		this.status = status;
	}
}