package chatbot.teamcity.web.bean;

import chatbot.teamcity.model.UserKey;
import jetbrains.buildServer.users.SUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatUserMappingBean {
	UserKey userKey;
	String reason;
}
