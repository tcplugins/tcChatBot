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

import static chatbot.teamcity.web.ChatBotConfigurationEditPageActionController.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import chatbot.teamcity.connection.client.slack.SlackChatClientFactory;
import chatbot.teamcity.exception.ChatClientConfigurationException;
import chatbot.teamcity.model.ChatClientConfig;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.util.StringUtil;

public abstract class ChatClientConfigAction {

	protected ProjectManager myProjectManager;

	public ChatClientConfigAction(@NotNull ProjectManager projectManager) {
		super();
		this.myProjectManager = projectManager;
	}

	protected ChatClientConfig getChatClientConfigFromRequest(@NotNull final HttpServletRequest request) throws ChatClientConfigurationException {
		
		String id = StringUtil.nullIfEmpty(request.getParameter(CONFIG_ID));
		
		String type = getParameterAsStringOrNull(request, CONFIG_TYPE, "The Type field must not be empty");
		String name = getParameterAsStringOrNull(request, CONFIG_NAME, "The Name field must not be empty");
		String token = getParameterAsStringOrNull(request, CONFIG_TOKEN, "The token field must not be empty");
		String keyword = getParameterAsStringOrNull(request, CONFIG_KEYWORD, "The keyword field must not be empty");
		String projectExternalId = getParameterAsStringOrNull(request, PROJECT_ID, "The projectId field must not be empty");
		boolean email = getParameterAsBoolean(request, CONFIG_EMAIL);
		
		Map<String,String> properties = new HashMap<>();
		properties.put(SlackChatClientFactory.TOKEN_KEY, token);
		properties.put(SlackChatClientFactory.KEYWORD_KEY, keyword);
		
		if (id == null || "_new".equals(id) ){
			return new ChatClientConfig(
					UUID.randomUUID().toString(),
					name,
					type,
					myProjectManager.findProjectByExternalId(projectExternalId).getProjectId(),
					email,
					properties
					);
		}
	
		return new ChatClientConfig(
				id, 
				name,
				type,
				myProjectManager.findProjectByExternalId(projectExternalId).getProjectId(),
				email,
				properties);
	}
	
	public boolean getParameterAsBoolean(HttpServletRequest request, String paramName) {
		String returnValue = StringUtil.nullIfEmpty(request.getParameter(paramName));
		if (returnValue == null || "".equals(returnValue.trim())) {
			return false;
		}
		return Boolean.parseBoolean(returnValue);
	}

	public String getParameterAsStringOrNull(HttpServletRequest request, String paramName, String errorMessage) throws ChatClientConfigurationException {
		String returnValue = StringUtil.nullIfEmpty(request.getParameter(paramName));
		if (returnValue == null || "".equals(returnValue.trim())) {
			throw new ChatClientConfigurationException(errorMessage);
		}
		return returnValue;
	}
	
	public abstract String getChatClientConfigAction();

	public boolean canProcess(@NotNull HttpServletRequest request) {
		return getChatClientConfigAction().equals(request.getParameter(ACTION_TYPE));
	}

}