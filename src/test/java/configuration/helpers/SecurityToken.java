package configuration.helpers;

import static config.Config.user;

/**
 * Created by kgr on 11/16/2016.
 */
public class SecurityToken {
    private static SecurityToken securityToken;
    private static String token;
    private static String token_backup;

    private SecurityToken() {
        RequestHelper requestHelper = new RequestHelper();
        token = requestHelper.getSecurityToken();
        token_backup = token;
        SessionsPool.registerSession(user, token);
    }

    public static String getToken() {
        if (securityToken == null)
            securityToken = new SecurityToken();
        return token;
    }

    public static void changeSession(SessionToken sessionToken) {
        token = sessionToken.getToken();
    }

    public static void revertSession() {
        token = token_backup;
    }

}
