package chatbot.teamcity.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Data;

@Data
public class User {
	
	@Nullable private long teamCityUserId;
	@NotNull  private UserKey chatUser;
	
	public User addTeamCityUserId(Long teamCityUserId) {
		this.teamCityUserId = teamCityUserId;
		return this;
	}
}
