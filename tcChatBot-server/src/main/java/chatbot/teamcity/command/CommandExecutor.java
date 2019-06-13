package chatbot.teamcity.command;

import java.util.List;

import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;

public interface CommandExecutor {
	
	/**
	 * A String to uniquely identify the command. <br>
	 * It must be unique as it is used as the key for finding a command to run
	 * @return command type key
	 */
	public String getCommandType();
	
	/**
	 * When a request comes in <code>supportsCommand()</code> is called 
	 * against every executor until a match is found and 
	 * the first matching command is executed. <br>
	 * The Execution order specifies the order in which the commands are searched.
	 * More specific commands should have a lower value (closer to 1) than 
	 * less specific commands of the same word so that greedy regexes don't hide them.<br>
	 * eg <br>
	 *  <i> run buildName / branchName on agentName</i><br>
	 * should have a lower ExecutionOrder than <br>
	 *  <i> run buildName</i>
	 * 
	 * @return an integer representing the order in which search for commands.
	 */
	public int getExecutionOrder();
	
	/**
	 * Controls the order in which commands are listed when help is requested.<br>
	 * It's often useful to show progressively more advanced command strings rather
	 * than the other way around. This
	 * 
	 * @return an integer representing the order in which to display help messages.
	 */
	public int getHelpOrder();
	public CommandResponse handleRequest(Context context, Request request);
	public boolean supportsCommand(String command);
	public List<String> getHelpTextLines();

}
