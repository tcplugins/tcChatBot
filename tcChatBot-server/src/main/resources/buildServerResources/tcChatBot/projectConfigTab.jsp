<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="afn" uri="/WEB-INF/functions/authz" %>
<%@ include file="/include-internal.jsp" %>

<div class="manageChatBots">
    <h2 class="noBorder">Chat Bots</h2>
    		<div class="grayNote">
				<p>A Chat Bot is for interacting with TeamCity via a Chat interface, eg Slack, MS Teams, Dischord, MatterMost, etc. 
				The ChatBot connects to your chat service, and listens for commands, which are then executed on the TeamCity server, 
				and the result sent back to the chat channel.</p> 
			</div>

    <c:if test="${userHasPermissionManagement}">
        <div class="add">
            <forms:addButton id="createNewChatBot" onclick="ChatBotPlugin.addChatBot('${projectExternalId}'); return false">Add new Chat Bot</forms:addButton>
        </div>
    </c:if>

    <bs:refreshable containerId="chatBots" pageUrl="${pageUrl}">
        <div class="repoList">
            <c:choose>
                <c:when test="${fn:length(chatConfigs) > 0}">
                    <table class="repoTable parametersTable">
                        <tr>
                            <th class="name" width="25%">Type</th>
                            <th class="name" width="50%">Name</th>
                            <c:if test="${userHasPermissionManagement}">
                                <th class="actions" colspan="3" width="25%">Actions</th>
                            </c:if>
                        </tr>
                        <c:forEach items="${chatConfigs}" var="item">
                                <tr class="chatBotInfo">
                                	<td>${item.config.clientType}</td>
                                	<td><c:out value="${item.config.name}"/></td>
                                    <c:if test="${userHasPermissionManagement && afn:permissionGrantedForProject(sProject, 'EDIT_PROJECT')}">
                                    
                                        <td class="edit">
                                            <a href="#" onclick='ChatBotPlugin.editChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${projectExternalId}" }); return false'>edit</a>
                                        </td>
                                       <td class="edit">
                                            <a href="#" onclick="ChatBotPlugin.deleteChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${projectExternalId}" }); return false">delete</a>
                                        </td>
                                        <td class="edit">
                                            <a href="#" onclick="ChatBotPlugin.restartChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${projectExternalId}" }); return false">restart</a>
                                        </td>
                                        
                                     </c:if>
                                    <c:if test="${userHasPermissionManagement && not afn:permissionGrantedForProject(sProject, 'EDIT_PROJECT')}">
                                        <td rowspan=3 class="grayNote edit"><span title="PROJECT_EDIT permission required">no permissions</span>
                                        </td>
                                    </c:if>
                                </tr>
                        </c:forEach>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="noChatBotsFound">
                        No Chat Bots have been created in this project yet.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </bs:refreshable>

    <bs:dialog dialogId="addChatBotDialog"
               dialogClass="addConfigDialog"
               title="Add Chat Bot"
               closeCommand="ChatBotPlugin.AddChatBotDialog.close()">
        <forms:multipartForm id="addChatBotForm"
                             action="/admin/chatBotAction.html"
                             targetIframe="hidden-iframe"
                             onsubmit="return ChatBotPlugin.AddChatBotDialog.doPost();">

            <table class="runnerFormTable">
            	<input type="hidden" id="chatbot.type" name="chatbot.type" value="slack"/>
                <tr>
                    <th>Name<l:star/></th>
                    <td>
                        <div><input type="text" size="40" maxlength="256" id="chatbot.name" name="chatbot.name"/></div>
                        <div>A name for this ChatBot. eg, <i>Slack Bot for Dev channels</i>.</div>
                    </td>
                </tr>
                <tr>
                    <th>Slack Token<l:star/></th>
                    <td>
                        <div><input type="text" size="40" maxlength="256" id="chatbot.secure:token" name="chatbot.secure:token"/></div>
                        <div>Slack token from the Slack bot creation page.</div>
                    </td>
                </tr>
                <tr>
                    <th>Command Keyword<l:star/></th>
                    <td>
                        <div><input type="text" id="chatbot.keyword" name="chatbot.keyword"/></div>
                        <div><p>Command keyword to which ChatBot responds. Must be the first word in a message for the ChatBot to respond. </p>
                        	 <p>eg, a keyword of <i>teamcity</i> would ensure ChatBot responds to commands like: <i>teamcity list projects</i></div>
                    </td>
                </tr>
                <tr>
                    <th>Auto-map by Email Address</th>
                    <td>
                        <div><input type="checkbox" id="chatbot.emailAutoMappingEnabled" name="chatbot.emailAutoMappingEnabled"/></div>
                        <div><p>Whether to automatically map Slack users to TeamCity users based on matching email addresses.</p> 
                        	 <p>Otherwise users will be asked to validate their TeamCity account via a link on their first interaction with the ChatBot.</p>
                        	 <p><b>Note:</b> If users can modify their email address in TeamCity or Slack, this is a security hole.</p>
                        </div>
                    </td>
                </tr>
                <div id="ajaxResult"></div>
            </table>
            
            <input type="hidden" id="chatbot.id" name="chatbot.id"/>
            <input type="hidden" name="action" id="ChatBotaction" value="addChatBot"/>
            <input type="hidden" name="projectId" id="projectId" value="${projectId}"/>
            <div class="popupSaveButtonsBlock">
                <forms:submit id="addChatBotDialogSubmit" label="Add Chat Bot"/>
                <forms:cancel onclick="ChatBotPlugin.AddChatBotDialog.close()"/>
            </div>
        </forms:multipartForm>
    </bs:dialog>

</div>
	
