#!/bin/bash

rsync -azlpv ~/git/tcChatBot/tcChatBot-server/src/main/resources/buildServerResources/tcChatBot/* teamcity:/opt/TeamCity/webapps/ROOT/plugins/tcChatBot/tcChatBot/.
