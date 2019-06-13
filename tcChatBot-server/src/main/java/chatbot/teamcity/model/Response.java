package chatbot.teamcity.model;

import java.util.ArrayList;
import java.util.List;

import chatbot.teamcity.service.MessageResponder;
import lombok.Data;

@Data
public class Response {
	
	List<String> messages;
	MessageResponder messenger; 
	Bundle bundle;
	
	public Response addMessage(String message) {
		if (this.messages == null) {
			this.messages = new ArrayList<>();
		}
		this.messages.add(message);
		return this;
	}
	
	public void setMessage(String message) {
		this.messages = new ArrayList<>();
		this.messages.add(message);
	}

	public void addMessages(List<String> messages) {
		if (this.messages == null) {
			this.messages = new ArrayList<>();
		}
		this.messages.addAll(messages);
	}
}
