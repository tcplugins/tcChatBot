package chatbot.teamcity.command;

import java.util.Comparator;

public class CommandExecutionRankingComparator  implements Comparator<CommandExecutor>{

	public int compare(CommandExecutor command1, CommandExecutor command2) {
		if (command1.getExecutionOrder() > command2.getExecutionOrder()){
			return -1;
		} else if (command1.getExecutionOrder() < command2.getExecutionOrder()){
			return 1;
		} else {
			return 0;
		}
	}
}
