package chatbot.teamcity.service;

public interface ChatClientRestarter {
	
	public void startChatClient(String configId);
	public void stopChatClient(String configId);
	public void restartChatClient(String configId);

}
