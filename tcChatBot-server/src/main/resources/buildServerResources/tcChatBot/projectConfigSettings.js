
ChatBotPlugin = {
    unlinkUser: function(data) {
    	ChatBotPlugin.UnlinkUserDialog.showDialog("Unlink User Mapping", 'unlinkUser', data);
    },
    addChatBot: function(projectId) {
    	ChatBotPlugin.AddChatBotDialog.showDialog("Add Chat Bot", 'addChatBot', { config: { configId: '_new', name: '', clientType: 'slack'}, projectId: projectId});
    },
    editChatBot: function(data) {
    	ChatBotPlugin.AddChatBotDialog.showDialog("Edit Chat Bot", 'editChatBot', data );
    },
    deleteChatBot: function(data) {
    	ChatBotPlugin.DeleteChatBotDialog.showDialog("Delete Chat Bot", 'deleteChatBot', data.config.configId, data.projectId);
    },
    restartChatBot: function(data) {
    	ChatBotPlugin.DeleteChatBotDialog.showDialog("Restart Chat Bot", 'restartChatBot', data.config.configId, data.projectId);
    },
    UnlinkUserDialog: OO.extend(BS.AbstractWebForm, OO.extend(BS.AbstractModalDialog, {
    	getContainer: function () {
    		return $('unlinkUserDialog');
    	},
    	
    	formElement: function () {
    		return $('unlinkUserForm');
    	},
    	
    	showDialog: function (title, action, data) {
    		$j("#unlinkUserForm input[id='ChatBotaction']").val(action);
    		$j("#unlinkUserDialog .dialogTitle").html(title);
    		$j("#unlinkUserDialog #unlinkUserDialogSubmit").val(title);
    		
    		this.cleanFields(data);
    		this.cleanErrors();
    		this.showCentered();
    	},
    	
    	cleanFields: function (data) {
    		$j("#unlinkUserForm input[id='user.userId']").val(data.userId);
    		$j("#unlinkUserForm input[id='user.chatClientType']").val(data.config.chatClientType);
    		$j("#unlinkUserForm input[id='user.chatClientGroup']").val(data.config.chatClientGroup);
    		$j("#unlinkUserForm input[id='user.chatUserName']").val(data.config.chatUserName);
    		
    		this.cleanErrors();
    	},
    	
    	cleanErrors: function () {
    		$j("#unlinkUserForm .error").remove();
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
    		var next = $j("#ajaxUserUnlinkResult").next();
    		if (next != null && next.prop("class") != null && next.prop("class").indexOf('error') > 0) {
    			next.text(message);
    		} else {
    			$j("#ajaxUserUnlinkResult").after("<p class='error'>" + message + "</p>");
    		}
    	},
    	
    	doValidate: function() {
    		var errorFound = false;
    		return !errorFound;
    	},
    	
    	doPost: function() {
    		this.cleanErrors();
    		
    		if (!this.doValidate()) {
    			return false;
    		}
    		
    		var parameters = {
    				action: $j("#unlinkUserForm #ChatBotaction").val(),
    				"user.chatClientType": $j("#unlinkUserForm input[id='user.chatClientType']").val(),
    				"user.chatClientGroup": $j("#unlinkUserForm input[id='user.chatClientGroup']").val(),
    				"user.chatClientUser": $j("#unlinkUserForm input[id='user.chatUserName']").val()
    		};
    		
    		var dialog = this;
    		
    		BS.ajaxRequest(window['base_uri'] + '/admin/chatBotAction.html', {
    			parameters: parameters,
    			onComplete: function(transport) {
    				var shouldClose = true;
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
    						} else if (responseTag.firstChild == null) {
    							shouldClose = false;
    							alert("Error: empty response");
    						}
    					}
    				}
    				if (shouldClose) {
    					dialog.close();
    					$("chatBotUserMappings").refresh();
    				}
    			}
    		});
    		
    		return false;
    	}
    })),    
    DeleteChatBotDialog: OO.extend(BS.AbstractWebForm, OO.extend(BS.AbstractModalDialog, {
    	getContainer: function () {
    		return $('deleteChatBotDialog');
    	},
    	
    	formElement: function () {
    		return $('deleteChatBotForm');
    	},
    	
    	showDialog: function (title, action, id, projectId) {
    		$j("#deleteChatBotForm input[id='ChatBotaction']").val(action);
    		$j("#deleteChatBotDialog .dialogTitle").html(title);
    		$j("#deleteChatBotDialog #deleteChatBotDialogSubmit").val(title);
    		$j("#deleteChatBotDialog span.deleteBlurb").hide();
    		$j("#deleteChatBotDialog span#" + action).show();
    		
    		this.cleanFields(id, projectId);
    		this.cleanErrors();
    		this.showCentered();
    	},
    	
    	cleanFields: function (id, projectId) {
    		$j("#deleteChatBotForm input[id='chatbot.id']").val(id);
    		$j("#deleteChatBotForm input[id='projectId']").val(projectId);
    		
    		this.cleanErrors();
    	},
    	
    	cleanErrors: function () {
    		$j("#deleteChatBotForm .error").remove();
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
    		var next = $j("#ajaxChatBotDeleteResult").next();
    		if (next != null && next.prop("class") != null && next.prop("class").indexOf('error') > 0) {
    			next.text(message);
    		} else {
    			$j("#ajaxChatBotDeleteResult").after("<p class='error'>" + message + "</p>");
    		}
    	},
    	
    	doValidate: function() {
    		var errorFound = false;
    		return !errorFound;
    	},
    	
    	doPost: function() {
    		this.cleanErrors();
    		
    		if (!this.doValidate()) {
    			return false;
    		}
    		
    		var parameters = {
    				action: $j("#deleteChatBotForm #ChatBotaction").val(),
    				"chatbot.id": $j("#deleteChatBotForm input[id='chatbot.id']").val(),
    				"projectId": $j("#deleteChatBotForm input[id='projectId']").val()
    		};
    		
    		var dialog = this;
    		
    		BS.ajaxRequest(window['base_uri'] + '/admin/chatBotAction.html', {
    			parameters: parameters,
    			onComplete: function(transport) {
    				var shouldClose = true;
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
    })),    
    AddChatBotDialog: OO.extend(BS.AbstractWebForm, OO.extend(BS.AbstractModalDialog, {
        getContainer: function () {
            return $('addChatBotDialog');
        },

        formElement: function () {
            return $('addChatBotForm');
        },

        showDialog: function (title, action, data) {
            $j("#addChatBotForm input[id='ChatBotaction']").val(action);
            $j("#addChatBotDialog .dialogTitle").html(title);
            $j("#addChatBotDialog #addChatBotDialogSubmit").val(title);
            this.cleanFields(data);
            this.cleanErrors();
            this.showCentered();
        },

        cleanFields: function (data) {
            this.cleanField(data, "configId", "chatbot.id");
            this.cleanField(data, "clientType", "chatbot.type");
            this.cleanField(data, "name", "chatbot.name");
            this.cleanField(data, "keyword", 'chatbot.keyword');
            this.cleanField(data, "secure_token", 'chatbot.secure_token');
            if (data.config.boolean_emailAutoMappingEnabled) {
            	$j("#addChatBotForm input[id='chatbot.emailAutoMappingEnabled']").prop('checked', data.config.boolean_emailAutoMappingEnabled == "true");
            } else {
            	$j("#addChatBotForm input[id='chatbot.emailAutoMappingEnabled']").prop('checked', false);
            }
            $j("#addChatBotForm input[id='projectId']").val(data.projectId);

            this.cleanErrors();
        },
        
        cleanField: function (data, dataName, inputName) {
        	if (data.config.hasOwnProperty(dataName)) {
        		$j("#addChatBotForm input[id='" + inputName + "']").val(data.config[dataName]);
        	} else {
        		$j("#addChatBotForm input[id='" + inputName + "']").val('');
        	}
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

            var name = $j('#addChatBotForm input[id="chatbot.name"]');
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
                "chatbot.id": $j("#addChatBotForm input[id='chatbot.id']").val(),
                "chatbot.type": $j("#addChatBotForm input[id='chatbot.type']").val(),
                "chatbot.name": $j("#addChatBotForm input[id='chatbot.name']").val(),
                "chatbot.secure_token": $j("#addChatBotForm input[id='chatbot.secure_token']").val(),
                "chatbot.keyword": $j("#addChatBotForm input[id='chatbot.keyword']").val(),
                "chatbot.emailAutoMappingEnabled": $j("#addChatBotForm input[id='chatbot.emailAutoMappingEnabled']").prop('checked'),
                "projectId": $j("#addChatBotForm #projectId").val(),
                action: $j("#addChatBotForm input[id='ChatBotaction']").val()
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
