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

import static chatbot.teamcity.web.ChatBotConfigurationEditPageActionController.CONFIG_ID;
import static chatbot.teamcity.web.ChatBotConfigurationEditPageActionController.PROJECT_ID;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.service.ChatClientConfigManager;
import chatbot.teamcity.service.ChatClientRestarter;
import chatbot.teamcity.web.ChatBotConfigurationEditPageActionController;
import jetbrains.buildServer.controllers.ActionMessages;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.web.openapi.ControllerAction;

public class DeleteChatClientConfigAction extends ChatClientConfigAction implements ControllerAction {

	private final ProjectManager myProjectManager;
	private final ChatClientRestarter myChatClientRestarter;
	private final ChatClientConfigManager myChatClientConfigManager;
	private final static String CHATBOT_ACTION = "deleteChatBot";

	public DeleteChatClientConfigAction(@NotNull ProjectManager projectManager,
									 @NotNull final ChatClientRestarter chatClientRestarter,
							   		 @NotNull final ChatClientConfigManager chatClientConfigManager,
							   		 @NotNull final ChatBotConfigurationEditPageActionController controller) {

		super(projectManager);
		myProjectManager = projectManager;
		myChatClientRestarter = chatClientRestarter;
		myChatClientConfigManager = chatClientConfigManager;
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
			String projectId = getParameterAsStringOrNull(request, PROJECT_ID, "Project ID field must not be empty");
			
			SProject sProject = this.myProjectManager.findProjectByExternalId(projectId);
			if (Objects.isNull(sProject)) {
				ajaxResponse.setAttribute("error", "Unable to find project for id: " + projectId);
				return;
			}
			myChatClientRestarter.stopChatClient(configId);
			myChatClientConfigManager.deleteConfig(sProject, configId);
		} catch (ChatClientConfigurationException e) {
			ajaxResponse.setAttribute("error", e.getMessage());
			return;
		}
		ActionMessages.getOrCreateMessages(request).addMessage("chatBotInfoUpdateResult",
			"ChatBot Config with ID '" + configId + "' successfully deleted");
		ajaxResponse.setAttribute("status", "OK");
		ajaxResponse.setAttribute("redirect", "false");
	}


}