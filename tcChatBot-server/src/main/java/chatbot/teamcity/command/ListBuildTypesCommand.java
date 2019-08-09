package chatbot.teamcity.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.service.BuildService;
import chatbot.teamcity.service.CommandService;
import chatbot.teamcity.service.UserService;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public class ListBuildTypesCommand extends BaseCommand implements CommandExecutor {
	
	private static final String SEARCH_STRING = "searchString";

	private final Pattern listBuildTypesPatternWithName = Pattern.compile("^list\\s+buildtypes\\s+(.+)$", Pattern.CASE_INSENSITIVE);
	private final Pattern listBuildTypesPattern = Pattern.compile("^list buildtypes$", Pattern.CASE_INSENSITIVE);
	private final Permissions permissions = new Permissions(Permission.VIEW_PROJECT); 

	private final UserService userService;
	private final BuildService buildService;

	public ListBuildTypesCommand(CommandService commandService, UserService userService, BuildService buildService) {
		Loggers.SERVER.info("ListBuildTypesCommand starting...");
		this.userService = userService;
		this.buildService = buildService;
		commandService.registerCommandExecutor(this);
	}

	@Override
	public String getCommandType() {
		return "listBuildTypes";
	}
	
	@Override
	public boolean supportsCommand(String command) {
		return listBuildTypesPatternWithName.matcher(command).matches() || listBuildTypesPattern.matcher(command).matches();
	}

	@Override
	public CommandResponse handleRequest(Context context, Request request) {
		Loggers.SERVER.debug("ListBuildTypesCommand :: Handling request: " + request);
		Response response = new Response();
		response.setMessenger(request.getMessenger());
		response.setBundle(request.getBundle());
		
		User user = request.getUser();
		updateContext(context, request);
		
		String buildTypeSearchString = context.getProperty(SEARCH_STRING);
		
		try {
			Loggers.SERVER.debug("ListBuildTypesCommand :: resolving user: " + user);
			SUser sUser = userService.resolveUser(user);
			Loggers.SERVER.debug("ListBuildTypesCommand :: User resolves to teamcity user: " + sUser.getUsername());

			Loggers.SERVER.debug("ListBuildTypesCommand :: finding permissioned buildTypes with search string '" + buildTypeSearchString + "'");
			List<SBuildType> buildTypes = buildService.findPermissionedBuildTypes(sUser, buildTypeSearchString, permissions);
			Loggers.SERVER.debug("ListBuildTypesCommand :: found " + buildTypes.size() + " permissioned buildTypes");
			
			if (buildTypes.isEmpty()) {
				Loggers.SERVER.debug("ListBuildTypesCommand :: User '" + user + "' is not permisioned to see any matching buildTypes. Search string was: '" + buildTypeSearchString + "'");
				response.addMessage("Sorry, {user}. No matching buildType found with the name '" + context.getProperty(SEARCH_STRING) + "'.");
			} else {
				buildTypes.forEach( buildType -> {
					response.addMessage("{fixedWidth}" + buildType.getExternalId() + "{/fixedWidth} : " + buildType.getName());
				});
			}

		} catch (UserNotFoundException ex) {
			Loggers.SERVER.debug("ListBuildTypesCommand :: User '" + user + "' does not map to a TeamCity user"); 
			response.addMessage("Sorry, I was unable to resolve your user in TeamCity.");
		}
		Loggers.SERVER.debug("ListBuildTypesCommand :: Command completed. Sending the following response back: " + response);
		return new CommandResponse(response, CommandState.FINISHED);
		
	}
	
	private void updateContext(Context context, Request request) {
		Loggers.SERVER.debug("ListBuildTypesCommand :: Extracting search string from '" + request.getMessage() + "' using pattern '" + listBuildTypesPattern.toString() + "'");
		Matcher matcherWithName = listBuildTypesPatternWithName.matcher(request.getMessage());
		Matcher matcher = listBuildTypesPattern.matcher(request.getMessage());
		if (matcherWithName.matches()) {
			Loggers.SERVER.debug("ListBuildTypesCommand :: Match found in search string '" + request.getMessage() + "' using pattern '" + listBuildTypesPatternWithName.toString() + "'");
			context.setProperty(SEARCH_STRING, matcherWithName.group(1));
		} else if (matcher.matches()) {
			Loggers.SERVER.debug("ListBuildTypesCommand :: Match found without search string '" + request.getMessage() + "' using pattern '" + listBuildTypesPattern.toString() + "'");
			context.setProperty(SEARCH_STRING, "");
		}
	}

	@Override
	public List<String> getHelpTextLines() {
		return Arrays.asList("{command}{keyword} list buildTypes{/command}",
				"{command}{keyword} list buildTypes <filter>{/command}");
	}

}
