package chatbot.teamcity.model;

import chatbot.teamcity.service.MessageResponder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class Request {
	
	User user;
	String message; 
	MessageResponder messenger; 
	Bundle bundle;

}
