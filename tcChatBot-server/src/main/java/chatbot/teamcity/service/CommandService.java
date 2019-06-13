package chatbot.teamcity.service;

import java.util.List;

import chatbot.teamcity.command.CommandExecutor;

public interface CommandService {
	
	public CommandExecutor findExecutorForCommand(String command);
	public void registerCommandExecutor(CommandExecutor executor);
	public List<CommandExecutor> getAllExecutorsRanked();
	public List<CommandExecutor> getAllExecutorsRankedByHelp();
}
