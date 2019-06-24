
# tcChatBot - A TeamCity ChatBot plugin 

tcChatBot connects to your Chat service, and listens for commands to run on the TeamCity server.

Version 1.0 supports Slack via the RTM WebSockets API. However, the design allows other chat services to be added easy,
 eg MS Teams, MatterMost, DisChord, WebEx, etc.
 
This plugin does not attempt to solve chat notifications. There are plenty of other plugins which can post messages to chat services. The screenshot below shows build status messages sent from the tcWebHooks Slack Compact template.

 
![Screenshot showing tcChatBot in action](https://raw.githubusercontent.com/tcplugins/tcChatBot/master/docs/images/tcChatBot_Screenshot.png "Chat with tcChatBot") 


### Adding a bot to slack.

1. [Create a slack bot](https://my.slack.com/services/new/bot) and get your slack token.  
1. Open Project settings in Teamcity, and click **Chat Bots**
1. Create a new Chat Bot, give it a name and paste in your slack bot API key.
1. Set the keyword. This is the keyword the bot responds to. eg, "tc" is the keyword used in the above screenshot.
1. Save and click restart.
1. Invite your bot to a channel and start chatting.
