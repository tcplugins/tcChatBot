/*******************************************************************************
 *
 *  Copyright 2016 Net Wolf UK
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
package chatbot.teamcity.web;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.controllers.BaseAjaxActionController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;

/**
 * This class simply holds the actions available at  "/admin/chatBotAction.html"
 * Actions need to inject this class and register themselves.
 */
public class ChatBotConfigurationEditPageActionController extends BaseAjaxActionController {
	
	public static final String ACTION_TYPE = "action";
    public static final String CONFIG_ID = "chatbot.id";
    public static final String CONFIG_NAME = "chatbot.name";
    public static final String CONFIG_TOKEN = "chatbot.secure_token";
    public static final String CONFIG_KEYWORD = "chatbot.keyword";
    public static final String CONFIG_EMAIL = "chatbot.emailAutoMappingEnabled";
    public static final String CONFIG_TYPE = "chatbot.type";
    public static final String PROJECT_ID = "projectId";
    
  public ChatBotConfigurationEditPageActionController(@NotNull final PluginDescriptor pluginDescriptor,
                                        	   @NotNull final WebControllerManager controllerManager) {
    super(controllerManager);
    controllerManager.registerController("/admin/chatBotAction.html", this);
  }
    
}