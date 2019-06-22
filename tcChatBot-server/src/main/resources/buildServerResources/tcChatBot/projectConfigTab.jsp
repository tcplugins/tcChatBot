<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="afn" uri="/WEB-INF/functions/authz" %>
<%@ include file="/include-internal.jsp" %>

<div class="manageRepos">
    <h2 class="noBorder">Chat Bots</h2>
    		<div class="grayNote">
				Some blah about chatbots
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
                            <th class="name">Name</th>
                            <th class="name">Information</th>
                            <c:if test="${userHasPermissionManagement}">
                                <th class="actions" colspan="2">Actions</th>
                            </c:if>
                        </tr>
                        projectExternalId : ${projectExternalId}
                        <c:forEach items="${chatConfigs}" var="config">
                                <tr class="repoInfo">
                                    <c:if test="${userHasPermissionManagement && afn:permissionGrantedForProject(sProject, 'EDIT_PROJECT')}">
                                        <td class="edit" rowspan=3>
                                            <a id="editServer" href="#">edit</a>
                                        </td>
                                    </c:if>
                                    <c:if test="${userHasPermissionManagement && not afn:permissionGrantedForProject(sProject, 'EDIT_PROJECT')}">
                                        <td rowspan=3 class="grayNote edit"><span title="PROJECT_EDIT permission required">edit</span></td>
                                        
                                    </c:if>
                                </tr>
                        </c:forEach>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="noChatBotsFound">
                        No Chat Bots have been created in this project.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </bs:refreshable>

    <bs:dialog dialogId="addChatBotDialog"
               dialogClass="addConfigDialog"
               title="Add Debian Repository"
               closeCommand="ChatBotPlugin.AddChatBotDialog.close()">
        <forms:multipartForm id="addChatBotForm"
                             action="/admin/chatBotAction.html"
                             targetIframe="hidden-iframe"
                             onsubmit="return ChatBotPlugin.AddChatBotDialog.doPost();">

            <table class="runnerFormTable">
                <tr>
                    <th>Name<l:star/></th>
                    <td>
                        <div><input type="text" id="chatbot.name" name="chatbot.name"/></div>
                    </td>
                    <th>Slack Token<l:star/></th>
                    <td>
                        <div><input type="text" id="chatbot.token" name="chatbot.token"/></div>
                    </td>
                </tr>
                <div id="ajaxResult"></div>
            </table>
            <input type="hidden" id="chatbot.type" name="chatbot.type" value="slack"/>
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
	
