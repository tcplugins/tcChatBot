package chatbot.teamcity.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.service.CommandService;

public class HelpCommand extends BaseCommand implements CommandExecutor {
	
	private static final Pattern helpPattern = Pattern.compile("^help.*$");
	
	private final CommandService commandService;

	public HelpCommand(CommandService commandService) {
		Loggers.SERVER.info("HelpCommand starting...");
		this.commandService = commandService;
		commandService.registerCommandExecutor(this);
	}

	@Override
	public String getCommandType() {
		return "help";
	}
	
	@Override
	public boolean supportsCommand(String command) {
		return helpPattern.matcher(command).matches();
	}

	@Override
	public CommandResponse handleRequest(Context context, Request request) {
		Loggers.SERVER.debug("HelpCommand :: Handling request: " + request);
		Response response = new Response();
		response.setMessenger(request.getMessenger());
		response.setBundle(request.getBundle());
		
		for (CommandExecutor command : this.commandService.getAllExecutorsRankedByHelp()) {
			Loggers.SERVER.debug("HelpCommand :: Adding help for command: " + command.getCommandType());
			response.addMessages(command.getHelpTextLines());
		}
		Loggers.SERVER.debug("HelpCommand :: Command completed. Sending the following response back: " + response);
		return new CommandResponse(response, CommandState.FINISHED);
		
	}
	
	@Override
	public int getExecutionOrder() {
		return 200;
	}

	@Override
	public List<String> getHelpTextLines() {
		return Arrays.asList("{command}{keyword} help{/command}");
	}

	@Override
	public int getHelpOrder() {
		return 1000;
	}

}
