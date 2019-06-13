package chatbot.teamcity.connection.client.slack.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImOpenResponse {

	private Boolean ok;
	private Channel channel;
	private String error;
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Channel {
		private String id;
	}
}