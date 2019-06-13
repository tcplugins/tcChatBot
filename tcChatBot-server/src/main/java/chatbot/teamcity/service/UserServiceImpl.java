package chatbot.teamcity.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.exception.InvalidMessageRequestException;
import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.User;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.model.ValidationHolder;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;

@Service
public class UserServiceImpl implements UserService {
	
	private final Map<UUID,ValidationHolder> validations = new HashMap<>();
	private final Map<UserKey,User> users = new HashMap<>(); 
	private final UserModel teamCityUserService;
	private final BuildService buildService;
	private final SBuildServer sBuildServer;
	private final UserMappingRepository userMappingService;
	
	
	public UserServiceImpl(UserModel teamCityUserService, BuildService buildService, SBuildServer sBuildServer, UserMappingRepository userMappingService) {
		this.teamCityUserService = teamCityUserService;
		this.buildService = buildService;
		this.sBuildServer = sBuildServer; 
		this.userMappingService = userMappingService;
	}
	

	@Override
	public SUser resolveUser(User user) throws UserNotFoundException {
		User u = findUser(user.getChatUser());
		SUser sUser = teamCityUserService.findUserById(u.getTeamCityUserId()); 
		if (sUser == null) {
			throw new UserNotFoundException("User not found in teamcity with ID: " + u.getTeamCityUserId());
		}
		return sUser;
	}

	@Override
	public boolean userHasPermissionForBuildType(User user, String buildTypeName, Permissions permissions) throws UserNotFoundException {
		return buildService.findPermissionedBuildType(resolveUser(user), buildTypeName, permissions) != null;
	}

	@Override
	public User findUser(UserKey chatUser) throws UserNotFoundException {
		
		if (users.containsKey(chatUser)) {
			return users.get(chatUser);
		} else {
			SUser sUser = userMappingService.findUser(chatUser);
			if (sUser != null) {
				users.put(chatUser, new User(chatUser).addTeamCityUserId(sUser.getId()));
				return users.get(chatUser);
			}
		}
		throw new UserNotFoundException(chatUser.toString());
	}

	@Override
	public String createValidationUrl(Request fromClient) {
		
		Errors e = fromClient.getUser().getChatUser().validate();
		if (e.hasErrors()) {
			throw new InvalidMessageRequestException(e.getAllErrors()
														.stream()
														.map(Object::toString)
														.collect(Collectors.joining(",")));
		}
		
		UUID uuid = UUID.randomUUID();
		validations.put(uuid, new ValidationHolder(uuid, fromClient.getUser().getChatUser(), fromClient.getMessenger().getConfigId()));
		Loggers.SERVER.debug("UUID is: " + uuid.toString());
		return stripTrailingSlash(this.sBuildServer.getRootUrl()) + "/chatbot/linkUser.html?token=" + uuid.toString();
	}

	@Override
	public ValidationHolder validateUser(UUID uuid, SUser teamCityUser) {
		if (validations.containsKey(uuid)) {
			Loggers.SERVER.debug("Validated teamcity user: '" + teamCityUser.getUsername() + " to validation UUID : " + uuid.toString());
			createUserMapping(validations.get(uuid).getUserKey(), teamCityUser, "Validated via user validation key");
			return validations.remove(uuid);
		}
		throw new UserNotFoundException("Unknown validation code. It may have already been used.");
	}

	@Override
	public User createUserMapping(UserKey chatUserKey, SUser sUser, String reason) {
		User user = new User(chatUserKey).addTeamCityUserId(sUser.getId());
		users.put(user.getChatUser(), user);
		userMappingService.setMapping(sUser, chatUserKey, reason);
		Loggers.SERVER.debug("Mapped user: '" + chatUserKey + " to teamCity UID: : " + sUser.getId());
		return user;
	}


    public static String stripTrailingSlash(String stringWithPossibleTrailingSlash){
    	if (stringWithPossibleTrailingSlash.endsWith("/")){
    		return stringWithPossibleTrailingSlash.substring(0, stringWithPossibleTrailingSlash.length()-1);
    	}
    	return stringWithPossibleTrailingSlash;
    	
    }


	@Override
	public User autoMapUser(UserKey chatUserKey, String emailAddress) {
		for (SUser sUser : teamCityUserService.getAllUsers().getUsers()) {
			if (sUser.getEmail().equalsIgnoreCase(emailAddress)) {
				Loggers.SERVER.debug("User found with matching email address: " + sUser.getUsername() + " -> " + emailAddress);
				return createUserMapping(chatUserKey, sUser, "Automapped via email address");
			}
		}
		throw new UserNotFoundException("No matching user in TeamCity with email address: " + emailAddress);
	}
	
}
