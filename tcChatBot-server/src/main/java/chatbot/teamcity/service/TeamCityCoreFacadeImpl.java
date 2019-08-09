package chatbot.teamcity.service;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jetbrains.buildServer.groups.SUserGroup;
import jetbrains.buildServer.groups.UserGroupManager;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserModel;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class TeamCityCoreFacadeImpl implements TeamCityCoreFacade {
    private final ProjectManager projectManager;
    private final UserGroupManager userGroupManager;
    private final PluginDescriptor pluginDescriptor;
    private final UserModel userModel;

    public TeamCityCoreFacadeImpl(ProjectManager projectManager,
                                  UserGroupManager userGroupManager, 
                                  PluginDescriptor pluginDescriptor, 
                                  UserModel userModel)
    {
        this.projectManager = projectManager;
        this.userGroupManager = userGroupManager;
        this.pluginDescriptor = pluginDescriptor;
        this.userModel = userModel;
    }

    @Nullable
    @Override
    public SProject findProjectByExtId(@Nullable String projectExtId) {
        return projectManager.findProjectByExternalId(projectExtId);
    }

    @Nullable
    @Override
    public SProject findProjectByIntId(String projectIntId) {
        return projectManager.findProjectById(projectIntId);
    }

    @NotNull
    @Override
    public List<SProject> getActiveProjects() {
        return projectManager.getActiveProjects();
    }

    @Override
    public void persist(@NotNull String projectId, @NotNull String description) {
    	findProjectByIntId(projectId).persist();
    }

    @NotNull
    @Override
    public Collection<SUserGroup> getAvailableGroups() {
        return userGroupManager.getUserGroups();
    }

    @Nullable
    @Override
    public SUserGroup findGroup(String groupKey) {
        return userGroupManager.findUserGroupByKey(groupKey);
    }

    @Nullable
    @Override
    public SUser getUser(long userId) {
        return userModel.findUserById(userId);
    }

    @NotNull
    @Override
    public String getPluginResourcesPath(@NotNull String path) {
        return pluginDescriptor.getPluginResourcesPath(path);
    }
}