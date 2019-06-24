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

import chatbot.teamcity.exception.UserNotFoundException;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.service.UserService;
import chatbot.teamcity.web.ChatBotConfigurationEditPageActionController;
import jetbrains.buildServer.controllers.ActionMessages;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.web.openapi.ControllerAction;

public class UnlinkUserConfigAction extends ChatClientConfigAction implements ControllerAction {

	private final UserService myUserService;
	private final static String CHATBOT_ACTION = "unlinkUser";

	public UnlinkUserConfigAction(
			@NotNull ProjectManager projectManager,
			@NotNull UserService userService,
			@NotNull final ChatBotConfigurationEditPageActionController controller) 
	{
		super(projectManager);
		myUserService = userService;
		controller.registerAction(this);
	}
	
	@Override
	public String getChatClientConfigAction() {
		return CHATBOT_ACTION;
	}

	public void process(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response,
			@Nullable final Element ajaxResponse) {
		
		try {
			
    		String chatClientType = getParameterAsStringOrNull(request, "user.chatClientType", "user.chatClientType field must not be empty");
			String chatClientGroup = getParameterAsStringOrNull(request, "user.chatClientGroup", "user.chatClientGroup field must not be empty");
			String chatClientUser = getParameterAsStringOrNull(request, "user.chatClientUser", "user.chatClientUser field must not be empty");
			
			UserKey userKey = new UserKey(chatClientType, chatClientGroup, chatClientUser);
			
			this.myUserService.removeUserMapping(userKey);
			
			ActionMessages.getOrCreateMessages(request).addMessage("chatBotInfoUpdateResult",
					"User Mapping '" + userKey.getMappingKey() + "' successfully deleted");
			ajaxResponse.setAttribute("status", "OK");
			ajaxResponse.setAttribute("redirect", "false");
			
		} catch (UserNotFoundException e) {
			ajaxResponse.setAttribute("error", "User not " + e.getMessage());
			return;
		}
		
	}


}