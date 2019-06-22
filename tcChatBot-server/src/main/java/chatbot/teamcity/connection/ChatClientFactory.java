package chatbot.teamcity.connection;

import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.MessageReceiver;

public interface ChatClientFactory {
	
	public String getChatClientType();
	public String getChatClientTypeName();
	public ChatClient createChatClient(ChatClientConfig config,  MessageReceiver messageReceiver);
	
	/** Get the list of extra configuration keys, which are stored in the 
	 *  {@link ChatClientConfig} properties map.<br>
	 *  
	 *  Only needs to return the list of "extra" properties which are client specific.<br>
	 *  Values that are secure, should have a key name prefixed with 'secure:' (include the colon).<br>
	 *  Boolean keys should be prefixed with 'boolean:', to give a hint to the UI to show as a checkbox. 
	 *  Currently secure booleans are not supported.
	 * 
	 * @return String[] of property keys.
	 */
	public String[] getExtraConfigKeys();
}
