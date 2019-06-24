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
    	<bs:messages key="chatBotInfoUpdateResult"/>
        <div class="repoList">
            <c:choose>
                <c:when test="${fn:length(chatConfigs) > 0}">
                    <table class="repoTable parametersTable filterTable">
                        <tr>
                            <th class="name" width="25%">Type</th>
                            <th class="name" width="50%">Name</th>
                            <th class="actions" colspan="3" width="25%">Actions</th>
                        </tr>
                        <c:forEach items="${chatConfigs}" var="item">
                                <tr class="chatBotInfo">
                                	<td>${item.config.clientType}</td>
                                	<td><c:out value="${item.config.name}"/></td>
                                    <c:if test="${userHasPermissionManagement && afn:permissionGrantedForProject(sProject, 'EDIT_PROJECT')}">
                                    
                                        <td class="edit" rowspan=2>
                                            <a href="#" onclick='ChatBotPlugin.editChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${projectExternalId}" }); return false'>edit</a>
                                        </td>
                                       <td class="edit" rowspan=2>
                                            <a href="#" onclick='ChatBotPlugin.deleteChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${projectExternalId}" }); return false'>delete</a>
                                        </td>
                                        <td class="edit" rowspan=2>
                                            <a href="#" onclick='ChatBotPlugin.restartChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${projectExternalId}" }); return false'>restart</a>
                                        </td>
                                        
                                     </c:if>
                                    <c:if test="${userHasPermissionManagement && not afn:permissionGrantedForProject(sProject, 'EDIT_PROJECT')}">
                                        <td rowspan=3 class="grayNote edit"><span title="PROJECT_EDIT permission required">no permissions</span>
                                        </td>
                                    </c:if>
                                </tr>
                                <tr><td colspan=2><b>Status:</b> <c:out value="${item.status}"/></td></tr>
                                <tr class="blankline"><td colspan=5 class="blankline">&nbsp</td></tr>
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
	<%@ include file="tcChatBotInclude.jsp" %>
</div>
	
