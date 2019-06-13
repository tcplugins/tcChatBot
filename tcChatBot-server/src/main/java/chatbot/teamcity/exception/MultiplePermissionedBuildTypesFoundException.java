package chatbot.teamcity.exception;

import java.util.List;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.auth.Permissions;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class MultiplePermissionedBuildTypesFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String buildTypeName;
	private final Permissions permissions;
	private final List<SBuildType> buildTypes;

}
