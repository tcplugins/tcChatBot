package chatbot.teamcity.command;

import java.util.Comparator;

public class CommandHelpRankingComparator  implements Comparator<CommandExecutor>{

	public int compare(CommandExecutor command1, CommandExecutor command2) {
		if (command1.getHelpOrder() > command2.getHelpOrder()){
			return -1;
		} else if (command1.getHelpOrder() < command1.getHelpOrder()){
			return 1;
		} else {
			return 0;
		}
	}
}
