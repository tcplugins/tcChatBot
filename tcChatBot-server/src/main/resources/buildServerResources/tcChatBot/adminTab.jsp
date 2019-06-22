<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="afn" uri="/WEB-INF/functions/authz" %>
<%@ include file="/include-internal.jsp" %>


	        <div class="repoList">
	        
	<div class="manageRepos">
	    <h2 class="noBorder">Debian Package Repositories</h2>
	    		<div class="grayNote">
				A Debian Package Repository provides the apt tools (apt-get, aptitude, synatpic) on Debian computers (or Ubuntu and other derivative distros) a location to locate and download software packages.<br>
				Creating a repository allows TeamCity to act as a Debian Package Repository serving the .deb files produced by your builds along with the meta-data required for the apt tools to locate packages.
				</div>
	
	<bs:messages key="botUpdateResult"/>
	
	        <div class="botList">
	            <c:choose>
	                <c:when test="${fn:length(chatBots) > 0}">
	                    <table class="repoTable settings">
	                        <tr>
	                            <th class="name">Name</th>
	                            <th class="name" colspan="2">Actions</th>
	                            <th class="name">Information</th>
	                        </tr>
	                        <c:forEach items="${chatBots}" var="mapping">
	                                <tr class="repoInfo">
	                                    <td class="name" rowspan=3 style="width:33%;">
	
	                                      <c:out value="${mapping.config.configId}"/>
	
	
	                                    </td>
	                                    <td class="edit" rowspan=3>
	                                    <c:out value="${mapping.config.clientType}"/>
                                        </td>
	                                    <td class="edit" rowspan=3>
		                                    <c:if test="${repo.permissionedOnProject}">
		                                            <a href="editDebianRepository.html?repo=${repo.debRepositoryConfiguration.repoName}">edit</a>
		                                    </c:if>
		                                    <c:if test="not ${repo.permissionedOnProject}">
		                                            <span class="grayNote" title="You are not permissioned to edit this project">edit</span>
		                                    </c:if>
                                        </td>
	                                    <td style="width:33%;">
	                                    	Builds Types: ${fn:length(repo.debRepositoryConfiguration.buildTypes)}
	                                    </td>
	                                </tr>
	                                <tr><td style="width:33%;">Artifact Filters: ${repo.debRepositoryStatistics.totalFilterCount}</td></tr>
	                                <tr><td style="width:33%;">Package Listings: ${repo.debRepositoryStatistics.totalPackageCount}</td></tr>
								<c:forEach items="${mapping.clients}" var="bot">
									<tr>
									<td><c:out value="${bot.configId}"/></td>
									<td><c:out value="${bot.chatClientType}"/></td>
									<td><c:out value="${bot.configId}"/></td>
									</tr>
								
								</c:forEach>
	                        </c:forEach>
	                    </table>
	                </c:when>
	                <c:otherwise>
	                    <div class="noReposFound">
	                        No Debian Repositories have been created yet. To create a repository, visit the "Debian Repositories" tab whilst editing a project.
	                    </div>
	                </c:otherwise>
	            </c:choose>
	        </div>
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
	                        	<td colspan="4">${mapping.key.name} (${mapping.key.username})
	                        </tr>
	                        	<c:forEach var="info" items="${mapping.value}">
							        <tr>
							        	<td>${info.chatClientName}</td>
							        	<td>${info.userKey.chatClientGroup}</td>
							        	<td>${info.userKey.chatUserName}</td>
							        	<td>${info.reason}</td>
							        </tr>
								</c:forEach>
	                        </c:forEach>
	                	</table>
	                </c:when>
	            <c:otherwise>
	            </c:otherwise>
	            </c:choose>
			</div>
	</div>