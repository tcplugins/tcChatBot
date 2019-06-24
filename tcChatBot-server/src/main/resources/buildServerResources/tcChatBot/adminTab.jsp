<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="afn" uri="/WEB-INF/functions/authz" %>
<%@ include file="/include-internal.jsp" %>


<div class="manageChatBots">
    		<div class="grayNote">
				<p>A Chat Bot is for interacting with TeamCity via a Chat interface, eg Slack, MS Teams, Dischord, MatterMost, etc. 
				The ChatBot connects to your chat service, and listens for commands, which are then executed on the TeamCity server, 
				and the result sent back to the chat channel.</p> 
			</div>

		<b>To create a ChatBot, please visit a Project Configuration Page, and click the ChatBots tab.</b>
	
    <bs:refreshable containerId="chatBots" pageUrl="${pageUrl}">
    	<bs:messages key="chatBotInfoUpdateResult"/>
        <div class="repoList">
            <c:choose>
                <c:when test="${fn:length(chatBots) > 0}">
                
                	The following Chat Bots have been configured.
                
                    <table class="repoTable parametersTable filterTable">

                        <c:forEach items="${chatBots}" var="chatConfigEntry">
                        
                        	<tr class="blankline"><td colspan=5 class="blankline">
                                    <admin:editProjectLink projectId="${chatConfigEntry.key.externalId}">
                                        <c:out value="${chatConfigEntry.key.name}"/>
                                    </admin:editProjectLink>
                        	</td></tr>
	                        <tr>
	                            <th class="name" width="25%">Type</th>
	                            <th class="name" width="50%">Name</th>
	                            <th class="actions" colspan="3" width="25%">Actions</th>
	                        </tr>
	                        <c:forEach items="${chatConfigEntry.value}" var="item">
	                                <tr class="chatBotInfo">
	                                	<td>${item.config.clientType}</td>
	                                	<td><c:out value="${item.config.name}"/></td>
	                                    <c:if test="${userHasPermissionManagement}">
	                                    
	                                        <td class="edit" rowspan=2>
	                                            <a href="#" onclick='ChatBotPlugin.editChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${chatConfigEntry.key.externalId}" }); return false'>edit</a>
	                                        </td>
	                                       <td class="edit" rowspan=2>
	                                            <a href="#" onclick='ChatBotPlugin.deleteChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${chatConfigEntry.key.externalId}" }); return false'>delete</a>
	                                        </td>
	                                        <td class="edit" rowspan=2>
	                                            <a href="#" onclick='ChatBotPlugin.restartChatBot({ "config": <c:out value="${item.json}" />, "projectId": "${chatConfigEntry.key.externalId}" }); return false'>restart</a>
	                                        </td>
	                                        
	                                     </c:if>
	                                    <c:if test="${not userHasPermissionManagement}">
	                                        <td rowspan=3 class="grayNote edit"><span title="PROJECT_EDIT permission required">no permissions</span>
	                                        </td>
	                                    </c:if>
	                                </tr>
	                                <tr><td colspan=2><b>Status:</b> <c:out value="${item.status}"/></td></tr>
	                                <tr class="blankline"><td colspan=5 class="blankline">&nbsp</td></tr>
	                        </c:forEach>
	                        <tr class="blankline"><td colspan=5 class="blankline">&nbsp</td></tr>
	                	</c:forEach>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="noChatBotsFound">
                        No Chat Bots have been created yet. To Create 
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </bs:refreshable>
    <bs:refreshable containerId="chatBotUserMappings" pageUrl="${pageUrl}">
	        <div class="userMappings">
	        	<h2>ChatBot to User Mappings</h2>
	            <c:choose>
	                <c:when test="${fn:length(chatUsers) > 0}">
	                    <table class="repoTable settings">
	                        <tr>
	                            <th class="name" colspan="4">Mappings</th>
	                        </tr>
	                        <c:forEach items="${chatUsers}" var="mapping">	 
	                        <tr>
	                        	<td colspan="5">${mapping.key.name} (${mapping.key.username})</td>

	                        </tr>
	                        	<c:forEach var="info" items="${mapping.value}">
							        <tr>
							        	<td>${info.chatClientName}</td>
							        	<td>${info.userKey.chatClientGroup}</td>
							        	<td>${info.userKey.chatUserName}</td>
							        	<td>${info.reason}</td>
		                        		<td class="edit">
		                                	<a href="#" onclick='ChatBotPlugin.unlinkUser({ "config": <c:out value="${info.json}" /> }); return false'>unlink user</a>
		                               	</td>
							        </tr>
								</c:forEach>
	                        </c:forEach>
	                	</table>
	                </c:when>
	            <c:otherwise>
	            </c:otherwise>
	            </c:choose>
			</div>
	</bs:refreshable>
	<%@ include file="tcChatBotInclude.jsp" %>
</div>