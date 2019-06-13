package chatbot.teamcity.connection.client;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import chatbot.teamcity.connection.ChatClient;
import chatbot.teamcity.connection.ChatClientFactory;
import chatbot.teamcity.connection.ChatClientManager;
import chatbot.teamcity.connection.ChatClientManagerImpl;
import chatbot.teamcity.connection.client.slack.SlackChatClientFactory;
import chatbot.teamcity.connection.client.slack.SlackUserService;
import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.service.MessageReceiver;
import chatbot.teamcity.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackChatClientFactoryTest {
	
	private static String token = "xoxb-361494744658-pqK9Rqj8mM7j4V5qRh03Mxkv";
	
	@Mock UserService userService;
	
	public static void main(String args[]) {
		SlackChatClientFactoryTest test = new SlackChatClientFactoryTest();
		test.testCreateChatClient();
	}

	public void testCreateChatClient() {
		MockitoAnnotations.initMocks(this);
		
		User user = new User(new UserKey("slack", "testGroup", "netwolfuk"));
		user.setTeamCityUserId(1l);
		
		when(userService.findUser(any())).thenThrow(new UserNotFoundException("blah"));
		
		SlackUserService slackUserService = new SlackUserService(new RestTemplate());
		
		ChatClientManager manager = new ChatClientManagerImpl();
		ChatClientFactory factory = new SlackChatClientFactory(manager, userService, slackUserService);
		ChatClientConfig config = new ChatClientConfig();
		config.setClientType("slack");
		config.setProperties(new HashMap<String, String>());
		config.getProperties().put("token", token);
		config.getProperties().put("uuid", "blah");
		config.getProperties().put("keyword", "teamcity");

		MessageReceiver messageReceiver = new MessageReceiver() {
			
			@Override
			public void receive(Request request) {
				log.info(request.toString());
				Response r = new Response();
				r.setBundle(request.getBundle());
				r.setMessage("a test");
				request.getMessenger().respond(r);
			}
		};
		
		ChatClient client = factory.createChatClient(config, messageReceiver);
		client.start();
		
        try {
            Thread.sleep(60 * 1000);
            client.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
