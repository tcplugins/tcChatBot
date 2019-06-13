package chatbot.teamcity.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import chatbot.teamcity.Loggers;
import chatbot.teamcity.command.CommandExecutionRankingComparator;
import chatbot.teamcity.command.CommandExecutor;
import chatbot.teamcity.command.CommandHelpRankingComparator;
import chatbot.teamcity.command.CommandNotFoundException;

@Service
public class CommandServiceImpl implements CommandService {
	
	private Map<String, CommandExecutor> executors = new HashMap<>();
	private Comparator<CommandExecutor> rankComparator = new CommandExecutionRankingComparator();
	private Comparator<CommandExecutor> helpComparator = new CommandHelpRankingComparator();
	private List<CommandExecutor> orderedExecutorsCollection = new ArrayList<>();
	private ArrayList<CommandExecutor> orderedHelpCollection;
	
	@Override
	public void registerCommandExecutor(CommandExecutor executor) {
		Loggers.SERVER.info("CommandExecutor registered: " + executor.getCommandType());
		this.executors.put(executor.getCommandType(), executor);
		this.orderedExecutorsCollection = new ArrayList<CommandExecutor>(executors.values());
		this.orderedHelpCollection = new ArrayList<CommandExecutor>(executors.values());
		Collections.sort(this.orderedExecutorsCollection, rankComparator);
		Collections.sort(this.orderedHelpCollection, helpComparator);
	}

	@Override
	public CommandExecutor findExecutorForCommand(String command) {
		for (CommandExecutor executor : getAllExecutorsRanked()) {
			if (executor.supportsCommand(command)) {
				return executor;
			}
		}
		throw new CommandNotFoundException(command);
	}

	@Override
	public List<CommandExecutor> getAllExecutorsRanked() {
		return this.orderedExecutorsCollection;
	}

	@Override
	public List<CommandExecutor> getAllExecutorsRankedByHelp() {
		return this.orderedHelpCollection;
	}

}
