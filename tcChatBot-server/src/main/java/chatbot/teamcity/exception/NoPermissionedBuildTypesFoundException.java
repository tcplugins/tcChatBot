package chatbot.teamcity.exception;

import jetbrains.buildServer.serverSide.auth.Permissions;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class NoPermissionedBuildTypesFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String buildTypeName;
	private final transient Permissions permissions;
}
