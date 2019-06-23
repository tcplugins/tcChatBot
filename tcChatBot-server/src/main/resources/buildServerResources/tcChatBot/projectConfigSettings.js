
ChatBotPlugin = {
   addChatBot: function(projectId) {
    	ChatBotPlugin.AddChatBotDialog.showDialog("Add Chat Bot", 'addChatBot', {id: '', name: '', projectId: projectId});
    },
    editChatBot: function(data) {
    	console.log(data);
    	alert (data);
    	ChatBotPlugin.AddChatBotDialog.showDialog("Edit Chat Bot", 'editChatBot', data );
    },
    AddChatBotDialog: OO.extend(BS.AbstractWebForm, OO.extend(BS.AbstractModalDialog, {
        getContainer: function () {
            return $('addChatBotDialog');
        },

        formElement: function () {
            return $('addChatBotForm');
        },

        showDialog: function (title, action, data) {
            $j("input[id='ChatBotaction']").val(action);
            $j(".dialogTitle").val(title);
            this.cleanFields(data);
            this.cleanErrors();
            this.showCentered();
        },

        cleanFields: function (data) {
            $j(".runnerFormTable input[id='chatbot.id']").val(data.config.confgId);
            $j(".runnerFormTable input[id='chatbot.type']").val(data.config.type);
            $j(".runnerFormTable input[id='chatbot.name']").val(data.config.name);
            $j(".runnerFormTable input[id='chatbot.keyword']").val(data.config.keyword);
            $j(".runnerFormTable input[id='chatbot.secure_token']").val(data.config.secure_token);
            $j(".runnerFormTable input[id='chatbot.emailAutoMappingEnabled']").val(data.config.emailAutoMappingEnabled);
            $j(".runnerFormTable input[id='projectId']").val(data.projectId);

            this.cleanErrors();
        },

        cleanErrors: function () {
            $j("#addChatBotForm .error").remove();
        },

        error: function($element, message) {
            var next = $element.next();
            if (next != null && next.prop("class") != null && next.prop("class").indexOf('error') > 0) {
                next.text(message);
            } else {
                $element.after("<p class='error'>" + message + "</p>");
            }
        },
        
        ajaxError: function(message) {
        	var next = $j("#ajaxResult").next();
        	if (next != null && next.prop("class") != null && next.prop("class").indexOf('error') > 0) {
        		next.text(message);
        	} else {
        		$j("#ajaxResult").after("<p class='error'>" + message + "</p>");
        	}
        },

        doValidate: function() {
            var errorFound = false;

            var name = $j('input[id="chatbot.name"]');
            if (name.val() == "") {
                this.error(name, "Please set the Chat Bot name");
                errorFound = true;
            }
            return !errorFound;
        },

        doPost: function() {
            this.cleanErrors();

            if (!this.doValidate()) {
                return false;
            }
            // TODO: This should probably be a jquery.each() to find the values.
            //       V1.0 only supports Slack, so this can be hard-coded for now.
            var parameters = {
                "chatbot.type": $j(".runnerFormTable input[id='chatbot.type']").val(),
                "chatbot.name": $j(".runnerFormTable input[id='chatbot.name']").val(),
                "chatbot.secure_token": $j(".runnerFormTable input[id='chatbot.secure_token']").val(),
                "chatbot.keyword": $j(".runnerFormTable input[id='chatbot.keyword']").val(),
                "chatbot.emailAutoMappingEnabled": $j(".runnerFormTable input[id='chatbot.emailAutoMappingEnabled']").val(),
                "projectId": $j("#addChatBotForm #projectId").val(),
                action: $j(".runnerFormTable input[id='ChatBotaction']").val()
            };

             var dialog = this;

     		 BS.ajaxRequest(window['base_uri'] + '/admin/chatBotAction.html', {
    			parameters: parameters,
    			onComplete: function(transport) {
    				var shouldClose = true;
    				var shouldRedirect = false;
    				if (transport != null && transport.responseXML != null) {
    					var response = transport.responseXML.getElementsByTagName("response");
    					if (response != null && response.length > 0) {
    						var responseTag = response[0];
    						var error = responseTag.getAttribute("error");
    						if (error != null) {
    							shouldClose = false;
    							dialog.ajaxError(error);
    						} else if (responseTag.getAttribute("status") == "OK") {
    							shouldClose = true;
    							if (responseTag.getAttribute("redirect") == "true") {
    								shouldRedirect = true;
    							}
    						} else if (responseTag.firstChild == null) {
    							shouldClose = false;
    							alert("Error: empty response");
    						}
    					}
    				}
    				if (shouldClose) {
    					dialog.close();
    					$("chatBots").refresh();
    				}

    			}
    		});
            return false;
        }
    }))
};
