package chatbot.teamcity.service;

import java.util.List;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SQueuedBuild;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

public interface BuildService {
	public SBuildType findPermissionedBuildType(SUser sUser, String searchText, Permissions permissions);
	public List<SBuildType> findPermissionedBuildTypes(SUser sUser, String searchText, Permissions permissions);
	public List<SProject> findPermissionedProjects(SUser sUser, String searchText, Permissions permissions);
	
	public SQueuedBuild queueBuild(SUser sUser, SBuildType sBuildType);
}
