package chatbot.teamcity.connection.client.slack.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackUserInfo {
	
	private Boolean ok;
	private User user;
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class User {
		private String id;
		private String name;
		private Boolean deleted;
		@JsonProperty(value="real_name")
		private String realName;
		private String tz;
		private Profile profile;
	}
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Profile {
        private String email;
	}

}