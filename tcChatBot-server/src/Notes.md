
## User Interaction Flow

#### User initiated
```
  User runs command. 
  Context initialised
  If command needs context or more info, 
      bot asks question.
	  info added to context
  else  
      Command executes
      If command needs more context, 
        bot asks question
        info added to context
        Command executes
  end if
  response send to user
  context cleared
```

## Components

ChatService - Takes input from ChatClient, runs command and returns response to ChatClient. Maintains Context etc.
ChatClient Manager - Creates instances of clients (bots). Handles the connections to from services.
Command Manager - Commands register here
Command Executer - Receives message from ChatService and executes commands. Sends responses.
User Manager - finds user, maps users, reads/writes from user mapping file.
BuildDataService - finds builds/buildsTypes and , gets build history/status.
PermissionService - checks if user has permission to run command.

ChatClient - Knows how to talk to the relevant service, eg slack.
ChatClientFactory - Knows how to create clients.