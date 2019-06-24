<%@ include file="/include.jsp" %>

<c:set var="pageTitle" value="Chat Bot - User Linker" scope="request"/>

<bs:page>
  <jsp:attribute name="head_include">
    <bs:linkCSS>
      /css/admin/adminMain.css
      ${teamcityPluginResourcesPath}tcChatBot/css/tcChatBot.css
    </bs:linkCSS>
    <script type="text/javascript">
      BS.Navigation.items = [
        {title: "Chat Bots - Account Linking", selected: true}
      ];
    </script>
  </jsp:attribute>

  <jsp:attribute name="body_include">
  
	<div class="manageRepos">
			<div>

			<c:choose>
                <c:when test="${not empty sUser.name}">			
					Hi <c:out value="${sUser.name}" />.
				</c:when>
                <c:otherwise>
					Hi <c:out value="${sUser.username}" />.
                </c:otherwise>
            </c:choose>			
			<c:choose>
                <c:when test="${empty error}">
            	    Your account has been successfully linked with the following identifiers. 
            	     
            	    <p><b>Type:</b> <c:out value="${validation.userKey.chatClientType}" /></p>
            	    <p><b>Group:</b> <c:out value="${validation.userKey.chatClientGroup}" /></p>
            	    <p><b>User:</b> <c:out value="${validation.userKey.chatUserName}" /></p>
            	    
            	    Note: These values might not be familiar to you. They could be internal identifiers used by your chat system. 
                
                </c:when>
                <c:otherwise>
                	<p>An error occurred when trying to link your chat and TeamCity accounts.</p>
					<c:out value="${error}" /> 
                </c:otherwise>
            </c:choose>
	        </div>
	
	</div>
  </jsp:attribute>
</bs:page>