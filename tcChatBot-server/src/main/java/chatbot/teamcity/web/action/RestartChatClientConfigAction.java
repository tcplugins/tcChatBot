/*******************************************************************************
 *
 *  Copyright 2019 Net Wolf UK
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  
 *******************************************************************************/
package chatbot.teamcity.web.action;

import static chatbot.teamcity.web.ChatBotUserLinkingController.ERROR_KEY;
import static chatbot.teamcity.web.ChatBotUserLinkingController.REDIRECT_KEY;
import static chatbot.teamcity.web.ChatBotUserLinkingController.STATUS_KEY;
import static chatbot.teamcity.web.ChatBotConfigurationEditPageActionController.CONFIG_ID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.service.ChatClientRestarter;
import chatbot.teamcity.web.ChatBotConfigurationEditPageActionController;
import jetbrains.buildServer.controllers.ActionMessages;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.web.openapi.ControllerAction;

public class RestartChatClientConfigAction extends ChatClientConfigAction implements ControllerAction {

	private static final String CHATBOT_ACTION = "restartChatBot";
	private final ChatClientRestarter myChatClientRestarter;

	public RestartChatClientConfigAction(
			@NotNull ProjectManager projectManager,
			@NotNull final ChatClientRestarter chatClientRestarter,
			@NotNull final ChatBotConfigurationEditPageActionController controller) 
	{
		super(projectManager);
		myProjectManager = projectManager;
		myChatClientRestarter = chatClientRestarter;
		controller.registerAction(this);
	}
	
	@Override
	public String getChatClientConfigAction() {
		return CHATBOT_ACTION;
	}

	public void process(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response,
			@Nullable final Element ajaxResponse) {
		
		String configId;
		try {
			configId = getParameterAsStringOrNull(request, CONFIG_ID, "Config ID field must not be empty");
		} catch (ChatClientConfigurationException e) {
			ajaxResponse.setAttribute(ERROR_KEY, e.getMessage());
			return;
		}
		
		myChatClientRestarter.restartChatClient(configId);
			
		ActionMessages.getOrCreateMessages(request).addMessage("chatBotInfoUpdateResult",
			"ChatBot Config with ID '" + configId + "' successfully restarted");
		ajaxResponse.setAttribute(STATUS_KEY, "OK");
		ajaxResponse.setAttribute(REDIRECT_KEY, "false");
	}


}