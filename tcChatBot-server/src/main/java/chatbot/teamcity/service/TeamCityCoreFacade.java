package chatbot.teamcity.service;
import jetbrains.buildServer.groups.SUserGroup;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.AuthorityHolder;
import jetbrains.buildServer.serverSide.auth.Role;
import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface TeamCityCoreFacade {

    //ROLES
    @NotNull
    Collection<SUserGroup> getAvailableGroups();

    @Nullable
    SUserGroup findGroup(String groupKey);

    @Nullable
    SUser getUser(long userId);

    //PROJECTS
    @Nullable
    SProject findProjectByExtId(@Nullable String projectExtId);

    @Nullable
    SProject findProjectByIntId(String projectIntId);

    @NotNull
    List<SProject> getActiveProjects();

    void persist(@NotNull String project, @NotNull String description);

    //Plugins
    @NotNull
    String getPluginResourcesPath(@NotNull String path);


}
