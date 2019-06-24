package chatbot.teamcity.service;

import java.util.UUID;

import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.User;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.model.ValidationHolder;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public interface UserService {
	
	public boolean userHasPermissionForBuildType(User user, String searchString, Permissions permissions);
	public User findUser(UserKey chatUserId) throws UserNotFoundException;
	public User autoMapUser(UserKey chatUserKey, String emailAddress);
	public String createValidationUrl(Request fromClient);
	public ValidationHolder validateUser(UUID uuid, SUser teamCityUser);
	public SUser resolveUser(User user) throws UserNotFoundException;
	public User createUserMapping(UserKey chatUserKey, SUser sUser, String reason);
	public User removeUserMapping(UserKey chatUserKey);

}
