package chatbot.teamcity.exception;

public class InvalidMessageRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public InvalidMessageRequestException(String e) {
		super(e);
	}

}
