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
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public class ListProjectsCommand extends BaseCommand implements CommandExecutor {
	
	final private Pattern listProjectsPatternWithName = Pattern.compile("^list projects (.+)$");
	final private Pattern listProjectsPattern = Pattern.compile("^list projects$");
	private final Permissions permissions = new Permissions(Permission.VIEW_PROJECT); 

	
	private final UserService userService;
	private final BuildService buildService;

	public ListProjectsCommand(CommandService commandService, UserService userService, BuildService buildService) {
		Loggers.SERVER.info("ListProjectsCommand starting...");
		this.userService = userService;
		this.buildService = buildService;
		commandService.registerCommandExecutor(this);
	}

	@Override
	public String getCommandType() {
		return "listProjects";
	}
	
	@Override
	public boolean supportsCommand(String command) {
		return listProjectsPatternWithName.matcher(command).matches() || listProjectsPattern.matcher(command).matches();
	}

	@Override
	public CommandResponse handleRequest(Context context, Request request) {
		Loggers.SERVER.debug("ListProjectsCommand :: Handling request: " + request);
		Response response = new Response();
		response.setMessenger(request.getMessenger());
		response.setBundle(request.getBundle());
		
		User user = request.getUser();
		updateContext(context, request);
		
		String projectSearchString = context.getProperty(PROJECT_SEARCH_STRING);
		
		try {
			Loggers.SERVER.debug("ListProjectsCommand :: resolving user: " + user);
			SUser sUser = userService.resolveUser(user);
			Loggers.SERVER.debug("ListProjectsCommand :: User resolves to teamcity user: " + sUser.getUsername());

			Loggers.SERVER.debug("ListProjectsCommand :: finding permissioned projects with search string '" + projectSearchString + "'");
			List<SProject> projects = buildService.findPermissionedProjects(sUser, projectSearchString, permissions);
			Loggers.SERVER.debug("ListProjectsCommand :: found " + projects.size() + " permissioned projects");
			
			if (projects.isEmpty()) {
				Loggers.SERVER.debug("ListProjectsCommand :: User '" + user + "' is not permisioned to see any matching buildTypes. Search string was: '" + projectSearchString + "'");
				response.addMessage("Sorry, {user}. No matching project found with the name '" + context.getProperty(PROJECT_SEARCH_STRING) + "'.");
			} else {
				projects.forEach( project -> {
					response.addMessage("{fixedWidth}" + project.getExternalId() + "{/fixedWidth} : " + project.getName());
				});
			}

		} catch (UserNotFoundException ex) {
			Loggers.SERVER.debug("ListProjectsCommand :: User '" + user + "' does not map to a TeamCity user"); 
			response.addMessage("Sorry, I was unable to resolve your user in TeamCity.");
		}
		Loggers.SERVER.debug("ListProjectsCommand :: Command completed. Sending the following response back: " + response);
		return new CommandResponse(response, CommandState.FINISHED);
		
	}
	
	private void updateContext(Context context, Request request) {
		Loggers.SERVER.debug("ListProjectsCommand :: Extracting search string from '" + request.getMessage() + "' using pattern '" + listProjectsPattern.toString() + "'");
		Matcher matcherWithName = listProjectsPatternWithName.matcher(request.getMessage());
		Matcher matcher = listProjectsPattern.matcher(request.getMessage());
		if (matcherWithName.matches()) {
			Loggers.SERVER.debug("ListProjectsCommand :: Match found in search string '" + request.getMessage() + "' using pattern '" + listProjectsPatternWithName.toString() + "'");
			context.setProperty(PROJECT_SEARCH_STRING, matcherWithName.group(1));
		} else if (matcher.matches()) {
			Loggers.SERVER.debug("ListProjectsCommand :: Match found without search string '" + request.getMessage() + "' using pattern '" + listProjectsPattern.toString() + "'");
			context.setProperty(PROJECT_SEARCH_STRING, "");
		}
	}

	@Override
	public List<String> getHelpTextLines() {
		return Arrays.asList("{command}{keyword} list projects{/command}",
				"{command}{keyword} list projects <filter>{/command}");
	}

}
