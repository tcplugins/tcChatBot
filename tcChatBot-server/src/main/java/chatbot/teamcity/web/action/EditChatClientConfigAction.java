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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.model.ChatClientConfig;
import chatbot.teamcity.service.ChatClientConfigManager;
import chatbot.teamcity.web.ChatBotConfigurationEditPageActionController;
import jetbrains.buildServer.controllers.ActionMessages;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.web.openapi.ControllerAction;

public class EditChatClientConfigAction extends ChatClientConfigAction implements ControllerAction {

	private final ChatClientConfigManager myChatClientConfigManager;
	private final static String EDIT_CHATBOT_ACTION = "editChatBot";

	public EditChatClientConfigAction(@NotNull ProjectManager projectManager,
							   		 @NotNull final ChatClientConfigManager chatClientConfigManager,
							   		 @NotNull final ChatBotConfigurationEditPageActionController controller) {

		super(projectManager);
		myProjectManager = projectManager;
		myChatClientConfigManager = chatClientConfigManager;
		controller.registerAction(this);
	}
	
	@Override
	public String getChatClientConfigAction() {
		return EDIT_CHATBOT_ACTION;
	}

	public void process(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response,
			@Nullable final Element ajaxResponse) {
		
		final ChatClientConfig clientConfig;
		try {
			clientConfig = getChatClientConfigFromRequest(request);
			myChatClientConfigManager.updateConfig(clientConfig, "Edited via UI");
		} catch (ChatClientConfigurationException e) {
			ajaxResponse.setAttribute("error", e.getMessage());
			return;
		}
		ActionMessages.getOrCreateMessages(request).addMessage("chatBotInfoUpdateResult",
			"ChatBot Config '" + clientConfig.getName() + "' successfully updated");
		ajaxResponse.setAttribute("status", "OK");
		ajaxResponse.setAttribute("redirect", "false");
	}


}