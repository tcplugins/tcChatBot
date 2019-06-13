package chatbot.teamcity.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import chatbot.teamcity.command.CommandExecutor;
import chatbot.teamcity.command.RunBuildCommand;

public class CommandServiceImplTest {

	@Test
	public void testRegisterCommandExecutor() {
		CommandService commandService = new CommandServiceImpl();
		CommandExecutor runCommand = new RunBuildCommand(commandService, null, null);
		
		CommandExecutor command = commandService.findExecutorForCommand("run build blah");
		assertEquals("runBuild", runCommand.getCommandType());
		assertEquals("runBuild", command.getCommandType());
	}

}
