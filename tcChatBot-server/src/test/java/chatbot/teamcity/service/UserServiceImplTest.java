package chatbot.teamcity.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import chatbot.teamcity.model.Request;
import chatbot.teamcity.model.Response;
import chatbot.teamcity.model.User;
import chatbot.teamcity.model.UserKey;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserServiceImplTest {

	@Test
	public void testUserHasPermissionForBuildType() {
		Permissions permissions = new Permissions(Permission.CANCEL_BUILD, Permission.RUN_BUILD, Permission.COMMENT_BUILD, Permission.LABEL_BUILD); 

		UserModel teamCityUserService = mock(UserModel.class);
		SUser teamCityUser = mock(SUser.class);
		
		SBuildType sBuildType = mock(SBuildType.class);
		SProject sProject = mock(SProject.class);
		SBuildServer sBuildServer = mock(SBuildServer.class);
		UserMappingRepository userMappingService = mock(UserMappingRepository.class);
		when(sProject.getProjectId()).thenReturn("project01");
		when(sProject.getProjectPath()).thenReturn(Arrays.asList(sProject));
		when(sBuildType.getProject()).thenReturn(sProject);
		when(sBuildServer.getRootUrl()).thenReturn("http://test.example.com/");
		when(teamCityUser.getId()).thenReturn(1L);
		
		BuildService buildService = mock(BuildService.class);
		when(buildService.findPermissionedBuildType(teamCityUser, "buildTypeExternalId", permissions)).thenReturn(sBuildType);
		
		UserService userService = new UserServiceImpl(teamCityUserService, buildService, sBuildServer, userMappingService);
		
		User user = userService.createUserMapping(new UserKey("test", "abcd", "netwolfuk"), teamCityUser, "Test");
		when(teamCityUser.getId()).thenReturn(1L);
		when(teamCityUserService.findUserById(1L)).thenReturn(teamCityUser);
		assertTrue(userService.userHasPermissionForBuildType(user, "buildTypeExternalId", permissions));
		
	}

	@Test
	public void testCreateValidationUrl() {
		Request request = createTestRequest();
		SBuildServer sBuildServer = mock(SBuildServer.class);
		when(sBuildServer.getRootUrl()).thenReturn("http://test.example.com/");
		UserService userService = new UserServiceImpl(null, null, sBuildServer, null);
		String url = userService.createValidationUrl(request);
		log.info(url);
		assertEquals(88, url.length());
		assertTrue(url.startsWith("http://"));
	}

	private Request createTestRequest() {
		User user = new User(new UserKey("test", "abcd", "netwolfuk"));
		Request request = new Request();
		request.setUser(user);
		request.setMessenger(createFakeMessenger());
		return request;
	}

	@Test
	public void testValidateUserAndFindUser() {
		Request request = createTestRequest();
		SBuildServer sBuildServer = mock(SBuildServer.class);
		UserMappingRepository userMappingService = mock(UserMappingRepository.class);
		when(sBuildServer.getRootUrl()).thenReturn("http://test.example.com/");
		UserService userService = new UserServiceImpl(null, null, sBuildServer, userMappingService);
		String url = userService.createValidationUrl(request);
		String uuid = url.substring(url.length()-36);
		SUser teamCityUser = mock(SUser.class);
		when(teamCityUser.getId()).thenReturn(1L);
		userService.validateUser(UUID.fromString(uuid), teamCityUser).getUserKey();
		assertEquals(request.getUser().getChatUser().getChatUserName(), 
					 userService.findUser(request.getUser().getChatUser()).getChatUser().getChatUserName());

	}

	private MessageResponder createFakeMessenger() {
		return new MessageResponder() {
			
			@Override
			public void respond(Response response) {
				log.info("here");
				
			}
			
			@Override
			public String getChatClientType() {
				return "test";
			}

			@Override
			public String getInstanceId() {
				return UUID.randomUUID().toString();
			}

			@Override
			public String getConfigId() {
				return UUID.randomUUID().toString();
			}

			@Override
			public String getKeyword() {
				return "teamcity";
			}
		};
	}
}
