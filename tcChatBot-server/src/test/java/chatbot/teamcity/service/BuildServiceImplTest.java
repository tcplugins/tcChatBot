package chatbot.teamcity.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public class BuildServiceImplTest {

	@Mock
	private ProjectManager projectManager;

	@Mock
	private SUser sUser;
	
	@Test
	public void testFindPermissionedProjects() {
		MockitoAnnotations.initMocks(this);
		List<SProject> projectList = setupProjects();
		when(sUser.getProjectsPermissions()).thenReturn(getProjectsPermissions());
		when(sUser.getGlobalPermissions()).thenReturn(getProjectsPermissions().get("p1"));
		when(projectManager.getActiveProjects()).thenReturn(projectList);
		BuildService buildService = new BuildServiceImpl(projectManager);
		List<SProject> projects  = buildService.findPermissionedProjects(sUser, "Project", new Permissions(Permission.VIEW_PROJECT));
		assertEquals(3, projects.size());
	}
	
	@Test
	public void testFindPermissionedProjects2() {
		MockitoAnnotations.initMocks(this);
		List<SProject> projectList = setupProjects();
		when(sUser.getProjectsPermissions()).thenReturn(getProjectsPermissions());
		when(sUser.getGlobalPermissions()).thenReturn(getProjectsPermissions().get("p2"));
		when(projectManager.getActiveProjects()).thenReturn(projectList);
		BuildService buildService = new BuildServiceImpl(projectManager);
		List<SProject> projects  = buildService.findPermissionedProjects(sUser, "Project", new Permissions(Permission.VIEW_PROJECT));
		assertEquals(2, projects.size());
	}

	private Map<String, Permissions> getProjectsPermissions() {
		Map<String, Permissions> projectsPermissions = new HashMap<>();
		projectsPermissions.put("p1", new Permissions(Permission.RUN_BUILD, Permission.VIEW_PROJECT));
		projectsPermissions.put("p2", Permissions.NO_PERMISSIONS);
		projectsPermissions.put("p3", new Permissions(Permission.VIEW_PROJECT));
		return projectsPermissions;
	}
	
	private List<SProject> setupProjects() {
		List<SProject> projList = new ArrayList<>();
		Arrays.asList("p1", "p2", "p3").forEach(name -> {
			SProject p = mock(SProject.class);
			when(p.getProjectId()).thenReturn(name);
			when(p.getName()).thenReturn("Project Name " + name);
			when(p.getExternalId()).thenReturn(name);
			when(projectManager.findProjectById(name)).thenReturn(p);
			projList.add(p);
		});
		return projList;
	}

}
