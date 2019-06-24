package chatbot.teamcity.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import chatbot.teamcity.exception.CommandNotPermissionedException;
import chatbot.teamcity.exception.MultiplePermissionedBuildTypesFoundException;
import chatbot.teamcity.exception.NoPermissionedBuildTypesFoundException;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SQueuedBuild;
import jetbrains.buildServer.serverSide.TriggeredByBuilder;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.Permissions;
import jetbrains.buildServer.users.SUser;

@Service
public class BuildServiceImpl implements BuildService {
	
	private final ProjectManager projectManager;

	public BuildServiceImpl(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@Override
	public SBuildType findPermissionedBuildType(SUser sUser, String searchText, Permissions permissions) {
		
		List<SBuildType> permissionedBuildTypes = findPermissionedBuildTypes(sUser, searchText, permissions);
		
		if (permissionedBuildTypes.size() > 1) {
			throw new MultiplePermissionedBuildTypesFoundException(searchText, permissions, permissionedBuildTypes);
		} else if (permissionedBuildTypes.isEmpty()) {
			throw new NoPermissionedBuildTypesFoundException(searchText, permissions);
		}
		
		return permissionedBuildTypes.get(0);
	}

	@Override
	public List<SBuildType> findPermissionedBuildTypes(SUser sUser, String searchText, Permissions permissions) {

		
		Map<String,Permissions> projectPermissions = getProjectsPermissions(sUser, permissions);
		
		/* If the externalID matches exactly, we have a match */
		SBuildType sBuildType = projectManager.findBuildTypeByExternalId(searchText);
		if (sBuildType != null && projectPermissions.get(sBuildType.getProjectId()).containsAny(permissions)) {
			return Arrays.asList(sBuildType);
		}
		
		/* Fetch all the projects once */
		Map<String,SProject> projects = new HashMap<>(); 
		projectPermissions.forEach((id, perms) -> {
			projects.put(id, projectManager.findProjectById(id));
		});
		
		Set<SBuildType> allPermissionedBuildTypes = new TreeSet<>();
		
		List<SBuildType> permissionedBuildTypes = new ArrayList<>();
		/* Next look for substring in externalID */
		projectPermissions.forEach((id, perms) -> {
			if (perms.containsAny(permissions)) {
				allPermissionedBuildTypes.addAll(projects.get(id).getOwnBuildTypes());
			}
		});
		
		/* Next look for case insensitive substring in externalID */
		if (permissionedBuildTypes.isEmpty()) {
			allPermissionedBuildTypes.forEach(b -> {
				if (b.getExternalId().toLowerCase().contains(searchText.toLowerCase())) {
					permissionedBuildTypes.add(b);
				}
			});
		}

		/* Next look for exact name */
		if (permissionedBuildTypes.isEmpty()) {
			allPermissionedBuildTypes.forEach(b -> {
				if (b.getName().equals(searchText)) {
					permissionedBuildTypes.add(b);
				}
			});
		}

		
		/* Next look for case insensitive exact name */
		if (permissionedBuildTypes.isEmpty()) {
			allPermissionedBuildTypes.forEach(b -> {
				if (b.getName().equalsIgnoreCase(searchText)) {
					permissionedBuildTypes.add(b);
				}
			});
		}

		
		/* Next look for substring in name */
		if (permissionedBuildTypes.isEmpty()) {
			allPermissionedBuildTypes.forEach(b -> {
				if (b.getName().contains(searchText)) {
					permissionedBuildTypes.add(b);
				}
			});
		}
		
		/* Next look for case insensitive substring in name */
		if (permissionedBuildTypes.isEmpty()) {
			allPermissionedBuildTypes.forEach(b -> {
				if (b.getName().toLowerCase().contains(searchText.toLowerCase())) {
					permissionedBuildTypes.add(b);
				}
			});
		}

		return permissionedBuildTypes;
	}

	private boolean isBuildTypePermissioned(SUser sUser, Permissions permissions, SBuildType buildType) {
		List<String> projectIds = new ArrayList<>();
		buildType.getProject().getProjectPath().forEach(project -> { projectIds.add(project.getProjectId()); });
		return sUser.getPermissionsGrantedForAnyOfProjects(projectIds).containsAny(permissions);
	}
	
	@Override
	public SQueuedBuild queueBuild(SUser sUser, SBuildType sBuildType) throws CommandNotPermissionedException {
		Permissions runBuildPermission = new Permissions(Permission.RUN_BUILD);
		if (isBuildTypePermissioned(sUser, runBuildPermission, sBuildType)) {
			TriggeredByBuilder tbb = new TriggeredByBuilder(sUser);
			tbb.addParameter("via", "ChatBot");
			return sBuildType.addToQueue(tbb.toString());
		}
		throw new CommandNotPermissionedException(sBuildType.getName(), runBuildPermission);
	}

	@Override
	public List<SProject> findPermissionedProjects(SUser sUser, String searchText, Permissions permissions) {
		
		List<SProject> permissionedProjects = new ArrayList<>();
		Map<String,SProject> projects = new HashMap<>(); 
		Map<String,Permissions> projectPermissions = getProjectsPermissions(sUser, permissions);
		
		/* Fetch all the projects once */
		projectPermissions.forEach((id, perms) -> {
			projects.put(id, projectManager.findProjectById(id));
		});
		
		/* Look for exact match of externalID */
		projectPermissions.forEach((id, perms) -> {
			if (projects.get(id).getExternalId().equalsIgnoreCase(searchText) && perms.containsAny(permissions)) {
				permissionedProjects.add(projects.get(id));
			}			
		});
		
		/* Next look for substring in externalID */
		if (permissionedProjects.isEmpty()) {
			projectPermissions.forEach((id, perms) -> {
				if (projects.get(id).getExternalId().contains(searchText) && perms.containsAny(permissions)) {
					permissionedProjects.add(projects.get(id));
				}			
			});
		}
		
		/* Next look for case insensitive substring in externalID */
		if (permissionedProjects.isEmpty()) {
			projectPermissions.forEach((id, perms) -> {
				if (projects.get(id).getExternalId().toLowerCase().contains(searchText.toLowerCase()) 
						&& perms.containsAny(permissions)) {
					permissionedProjects.add(projects.get(id));
				}			
			});
		}
		
		/* Next look for exact name */
		if (permissionedProjects.isEmpty()) {
			projectPermissions.forEach((id, perms) -> {
				if (projects.get(id).getName().equals(searchText) 
						&& perms.containsAny(permissions)) {
					permissionedProjects.add(projects.get(id));
				}
			});
		}
		
		/* Next look for case insensitive exact name */
		if (permissionedProjects.isEmpty()) {
			projectPermissions.forEach((id, perms) -> {
				if (projects.get(id).getName().equalsIgnoreCase(searchText)
						&& perms.containsAny(permissions)) {
					permissionedProjects.add(projects.get(id));
				}
			});
		}
		
		/* Next look for substring in name */
		if (permissionedProjects.isEmpty()) {
			projectPermissions.forEach((id, perms) -> {
				if (projects.get(id).getName().contains(searchText) 
						&& perms.containsAny(permissions)) {
					permissionedProjects.add(projects.get(id));
				}
			});
		}

		/* Next look for case insensitive substring in name */
		if (permissionedProjects.isEmpty()) {
			projectPermissions.forEach((id, perms) -> {
				if (projects.get(id).getName().toLowerCase().contains(searchText.toLowerCase()) 
						&& perms.containsAny(permissions)) {
					permissionedProjects.add(projects.get(id));
				}
			});
		}		

		return permissionedProjects;
	}

	private Map<String, Permissions> getProjectsPermissions(SUser sUser, Permissions permissions) {
		if (sUser.getGlobalPermissions().containsAny(permissions)) {
			return projectManager.getActiveProjects().stream()
													 .collect(Collectors.toMap(p -> p.getProjectId(), p-> permissions));
		}
		return sUser.getProjectsPermissions();
	}
	
}
