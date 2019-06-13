package chatbot.teamcity.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.exception.CommandNotPermissionedException;
import chatbot.teamcity.exception.MultiplePermissionedBuildTypesFoundException;
import chatbot.teamcity.exception.NoPermissionedBuildTypesFoundException;
import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.service.BuildService;
import chatbot.teamcity.service.CommandService;
import chatbot.teamcity.service.UserService;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SQueuedBuild;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public class RunBuildCommand implements CommandExecutor {
	
	private static final String BUILD_SEARCH_STRING = "buildSearchString";
	private static final String BUILD_TYPE_NAME = "buildTypeName";
	private static final String BUILD_TYPE_EXTERNAL_ID = "buildTypeExternalId";

	final private Pattern runBuildPattern = Pattern.compile("^run (.+)$");
	private final Permissions permissions = new Permissions(Permission.RUN_BUILD); 

	
	private final UserService userService;
	private final BuildService buildService;

	public RunBuildCommand(CommandService commandService, UserService userService, BuildService buildService) {
		Loggers.SERVER.info("RunBuildCommand starting...");
		this.userService = userService;
		this.buildService = buildService;
		commandService.registerCommandExecutor(this);
	}

	@Override
	public String getCommandType() {
		return "runBuild";
	}
	
	@Override
	public boolean supportsCommand(String command) {
		return runBuildPattern.matcher(command).matches();
	}

	@Override
	public CommandResponse handleRequest(Context context, Request request) {
		Loggers.SERVER.debug("RunBuildCommand :: Handling request: " + request);
		Response response = new Response();
		response.setMessenger(request.getMessenger());
		response.setBundle(request.getBundle());
		
		User user = request.getUser();
		updateContext(context, request);
		
		String buildTypeSearchString = context.getProperty(BUILD_SEARCH_STRING);
		
		try {
			Loggers.SERVER.debug("RunBuildCommand :: resolving user: " + user);
			SUser sUser = userService.resolveUser(user);
			Loggers.SERVER.debug("RunBuildCommand :: User resolves to teamcity user: " + sUser.getName());

			Loggers.SERVER.debug("RunBuildCommand :: finding permissioned buildType with search string '" + buildTypeSearchString + "'");
			SBuildType sBuildType = buildService.findPermissionedBuildType(sUser, buildTypeSearchString, permissions);
			Loggers.SERVER.debug("RunBuildCommand :: found permissioned buildType '" + sBuildType.getExternalId() + "'");

			try {
				Loggers.SERVER.debug("RunBuildCommand :: Requesting build queueing for buildType '" + sBuildType.getExternalId() + "'");
				SQueuedBuild sQueuedBuild = buildService.queueBuild(sUser, sBuildType);
				Loggers.SERVER.debug("RunBuildCommand :: Build queued and is number '" + sQueuedBuild.getOrderNumber() + "' in the queue");
				response.addMessage("Build '" + sBuildType.getName() + "' queued by " + sQueuedBuild.getTriggeredBy().getAsString() + ".");
			} catch (CommandNotPermissionedException ex) {
				Loggers.SERVER.debug("RunBuildCommand :: User '" + sUser.getUsername() + "' is not permisioned to queue buildType " + sBuildType.getName());
				response.addMessage("Sorry, {user}. You don't have permission to execute build '" + sBuildType.getName() + "'.");
			}
		} catch (NoPermissionedBuildTypesFoundException ex) {
			Loggers.SERVER.debug("RunBuildCommand :: User '" + user + "' is not permisioned to see any matching buildTypes. Search string was: '" + buildTypeSearchString + "'");
			response.addMessage("Sorry, {user}. No matching build found with the name '" + context.getProperty(BUILD_SEARCH_STRING) + "'.");
		} catch (MultiplePermissionedBuildTypesFoundException ex) {
			Loggers.SERVER.debug("RunBuildCommand :: Too many permisioned builds match the search criteria. Search string was: '" + buildTypeSearchString + "'");
			response.addMessage("{user}. I found " + ex.getBuildTypes().size() + " builds which match the name '" + context.getProperty(BUILD_SEARCH_STRING) + "'.");
			response.addMessage("Please be more specific, or to see a list matching builds send {command}" 
								+ request.getMessenger().getKeyword() + " list buildTypes " 
								+ context.getProperty(BUILD_SEARCH_STRING) + "{/command}");
		} catch (UserNotFoundException ex) {
			Loggers.SERVER.debug("RunBuildCommand :: User '" + user + "' does not map to a TeamCity user"); 
			response.addMessage("Sorry, I was unable to resolve your user in TeamCity.");
		}
		Loggers.SERVER.debug("RunBuildCommand :: Command completed. Sending the following response back: " + response);
		return new CommandResponse(response, CommandState.FINISHED);
		
	}
	
	private void updateContext(Context context, Request request) {
		Loggers.SERVER.debug("RunBuildCommand :: Extracting search string from '" + request.getMessage() + "' using pattern '" + runBuildPattern.toString() + "'");
		Matcher matcher = runBuildPattern.matcher(request.getMessage());
		if (matcher.matches()) {
			Loggers.SERVER.debug("RunBuildCommand :: Match found in search string '" + request.getMessage() + "' using pattern '" + runBuildPattern.toString() + "'");
			context.setProperty(BUILD_SEARCH_STRING, matcher.group(1));
		}
	}

	@Override
	public int getExecutionOrder() {
		return 100;
	}

	@Override
	public List<String> getHelpTextLines() {
		return Arrays.asList("{command}{keyword} run <filter>{/command}");
	}

	@Override
	public int getHelpOrder() {
		return 100;
	}

}
