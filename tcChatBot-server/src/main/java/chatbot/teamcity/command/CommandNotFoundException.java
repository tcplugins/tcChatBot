package chatbot.teamcity.command;

public class CommandNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CommandNotFoundException(String name) {
		super(name);
	}

}
