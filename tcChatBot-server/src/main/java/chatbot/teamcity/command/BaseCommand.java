package chatbot.teamcity.command;

public abstract class BaseCommand implements CommandExecutor {

	protected static final String BUILD_SEARCH_STRING = "buildSearchString";
	protected static final String BUILD_TYPE_NAME = "buildTypeName";
	protected static final String BUILD_TYPE_EXTERNAL_ID = "buildTypeExternalId";
	protected static final String PROJECT_NAME = "projectName";
	protected static final String PROJECT_SEARCH_STRING = "projectSearchString";
	protected static final String PROJECT_EXTERNAL_ID = "projectExternalId";

	@Override
	public int getExecutionOrder() {
		return 100;
	}
	
	@Override
	public int getHelpOrder() {
		return 100;
	}
	
}
