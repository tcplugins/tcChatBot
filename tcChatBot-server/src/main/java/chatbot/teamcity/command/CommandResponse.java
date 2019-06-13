package chatbot.teamcity.command;

import chatbot.teamcity.model.Response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CommandResponse {
	private Response response;
	private CommandState state;
}
