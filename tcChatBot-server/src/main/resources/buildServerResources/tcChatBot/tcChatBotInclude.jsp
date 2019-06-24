    <bs:dialog dialogId="addChatBotDialog"
               dialogClass="addConfigDialog"
               title="Add Chat Bot"
               closeCommand="ChatBotPlugin.AddChatBotDialog.close()">
        <forms:multipartForm id="addChatBotForm"
                             action="/admin/chatBotAction.html"
                             targetIframe="hidden-iframe"
                             onsubmit="return ChatBotPlugin.AddChatBotDialog.doPost();">

            <table class="runnerFormTable">
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
                        <div><input type="text" size="40" maxlength="256" id="chatbot.secure_token" name="chatbot.secure_token"/></div>
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
            	<input type="hidden" id="chatbot.type" name="chatbot.type" value="slack"/>
	            <input type="hidden" id="chatbot.id" name="chatbot.id"/>
	            <input type="hidden" name="action" id="ChatBotaction" value="addChatBot"/>
	            <input type="hidden" name="projectId" id="projectId" value="${projectId}"/>
            </table>
            
            <div class="popupSaveButtonsBlock">
                <forms:submit id="addChatBotDialogSubmit" label="Add Chat Bot"/>
                <forms:cancel onclick="ChatBotPlugin.AddChatBotDialog.close()"/>
            </div>
        </forms:multipartForm>
    </bs:dialog>

    <bs:dialog dialogId="deleteChatBotDialog"
               dialogClass="deleteChatBotDialog"
               title="Confirm Chat Bot deletion"
               closeCommand="ChatBotPlugin.DeleteChatBotDialog.close()">
        <forms:multipartForm id="deleteChatBotForm"
                             action="/admin/chatBotAction.html"
                             targetIframe="hidden-iframe"
                             onsubmit="return ChatBotPlugin.DeleteChatBotDialog.doPost();">

            <table class="runnerFormTable">
                <tr><td>
                	<span class="deleteBlurb" id="deleteChatBot">Deleting a Chat Bot configuration attempts to stop the bot and then removes all configuration.</span>
                	<span class="deleteBlurb" id="restartChatBot">Start/Restart chat bot instance.</span>
                        <div id="ajaxChatBotDeleteResult"></div>
                </td></tr>
            </table>
            <input type="hidden" id="chatbot.id" name="chatbot.id"/>
            <input type="hidden" name="action" id="ChatBotaction" value=""/>
            <input type="hidden" name="projectId" id="projectId" value="${projectId}"/>
            <div class="popupSaveButtonsBlock">
                <forms:submit id="deleteChatBotDialogSubmit" label="Delete Chat Bot"/>
                <forms:cancel onclick="ChatBotPlugin.DeleteChatBotDialog.close()"/>
            </div>
        </forms:multipartForm>
    </bs:dialog>

    <bs:dialog dialogId="unlinkUserDialog"
               dialogClass="unlinkUserDialog"
               title="Confirm Unlinking User"
               closeCommand="ChatBotPlugin.UnlinkUserDialog.close()">
        <forms:multipartForm id="unlinkUserForm"
                             action="/admin/chatBotAction.html"
                             targetIframe="hidden-iframe"
                             onsubmit="return ChatBotPlugin.UnlinkUserDialog.doPost();">

            <table class="runnerFormTable">
                <tr><td>
                	<span>Unlink User Account from ChatBot?</span>
                        <div id="ajaxUnlinkUserResult"></div>
                </td></tr>
            </table>
            <input type="hidden" id="user.chatClientType" name="user.chatClientType"/>
            <input type="hidden" id="user.chatClientGroup" name="user.chatClientGroup"/>
            <input type="hidden" id="user.chatUserName" name="user.chatUserName"/>
            <input type="hidden" name="action" id="ChatBotaction" value=""/>
            <div class="popupSaveButtonsBlock">
                <forms:submit id="unlinkUserDialogSubmit" label="Unlink User"/>
                <forms:cancel onclick="ChatBotPlugin.UnlinkUserDialog.close()"/>
            </div>
        </forms:multipartForm>
    </bs:dialog>