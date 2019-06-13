package chatbot.teamcity.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import chatbot.teamcity.exception.CommandNotPermissionedException;
import chatbot.teamcity.exception.NoPermissionedBuildTypesFoundException;
import chatbot.teamcity.model.Bundle;
import chatbot.teamcity.model.Context;
import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.User;
import chatbot.teamcity.model.UserKey;
import chatbot.teamcity.service.BuildService;
import chatbot.teamcity.service.CommandService;
import chatbot.teamcity.service.MessageResponder;
import chatbot.teamcity.service.UserService;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SQueuedBuild;
import jetbrains.buildServer.serverSide.TriggeredBy;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public class RunBuildCommandTest {
	@Mock CommandService commandService;
	@Mock MessageResponder responder;
	@Mock UserService userService;
	@Mock BuildService buildService;
	@Mock SBuildType sBuildType;
	
	User user;
	Permissions permissions;
	List<SBuildType> sBuildTypes;
	
	@Before
	public void setup() {
		permissions = new Permissions(Permission.CANCEL_BUILD, Permission.RUN_BUILD, Permission.COMMENT_BUILD, Permission.LABEL_BUILD); 
		MockitoAnnotations.initMocks(this);
		when(sBuildType.getName()).thenReturn("Test Build 1");
		when(sBuildType.getExternalId()).thenReturn("TestBuild1");
		
		user = new User(new UserKey(responder.getChatClientType(), "testGroup", "netwolfuk"));
		user.setTeamCityUserId(1l);
	}

	@Test
	public void testUserCannotExecuteBuild() {
		SUser sUser = mock(SUser.class);
		when(sUser.getName()).thenReturn("my user");
		when(userService.resolveUser(any())).thenReturn(sUser);
		when(buildService.findPermissionedBuildType(any(), eq("Test Build 1"), any())).thenReturn(sBuildType);
		when(buildService.queueBuild(any(), any())).thenThrow(new CommandNotPermissionedException("Test Build 1", permissions));
		
		CommandExecutor runBuild = new RunBuildCommand(commandService, userService, buildService);
		Context context = new Context();
		Bundle bundle = new Bundle();
		
		CommandResponse response = runBuild.handleRequest(context, new Request(user, "run Test Build 1", responder, bundle));
		assertEquals("Sorry, {user}. You don't have permission to execute build 'Test Build 1'.", response.getResponse().getMessages().get(0));
		verify(buildService, times(1)).queueBuild(any(), eq(sBuildType));
	}

	@Test
	public void testUserCanExecuteBuild() {
		SUser sUser = mock(SUser.class);
		when(sUser.getName()).thenReturn("my user");
		when(userService.resolveUser(any())).thenReturn(sUser);
		
		CommandExecutor runBuild = new RunBuildCommand(commandService, userService, buildService);
		SQueuedBuild sQueuedBuild = mock(SQueuedBuild.class);
		TriggeredBy triggeredBy = mock(TriggeredBy.class);
		when(sQueuedBuild.getTriggeredBy()).thenReturn(triggeredBy);
		when(triggeredBy.getAsString()).thenReturn("blahblah");
		
		when(userService.userHasPermissionForBuildType(user, "TestBuild1", permissions)).thenReturn(true);
		when(buildService.findPermissionedBuildType(any(), eq("Test Build 1"), any())).thenReturn(sBuildType);
		when(buildService.queueBuild(any(), eq(sBuildType))).thenReturn(sQueuedBuild);
		
		Context context = new Context();
		Bundle bundle = new Bundle();
		
		CommandResponse response = runBuild.handleRequest(context, new Request(user, "run Test Build 1", responder, bundle));
		assertEquals("Build 'Test Build 1' queued by blahblah.", response.getResponse().getMessages().get(0));
		verify(buildService, times(1)).queueBuild(any(), eq(sBuildType));
	}
	
	@Test
	public void testBuildNotFound() {
		SUser sUser = mock(SUser.class);
		when(sUser.getName()).thenReturn("my user");
		when(userService.resolveUser(any())).thenReturn(sUser);
		
		when(buildService.findPermissionedBuildType(any(), eq("Test Build 1"), any())).thenThrow(new NoPermissionedBuildTypesFoundException("TestBuild1", permissions));
		CommandExecutor runBuild = new RunBuildCommand(commandService, userService, buildService);
		Context context = new Context();
		Bundle bundle = new Bundle();
		
		CommandResponse response = runBuild.handleRequest(context, new Request(user, "run Test Build 1", responder, bundle));
		assertEquals("Sorry, {user}. No matching build found with the name 'Test Build 1'.", response.getResponse().getMessages().get(0));
		verify(buildService, times(0)).queueBuild(any(), eq(sBuildType));
		verify(commandService).registerCommandExecutor(runBuild);
	}
	
}
