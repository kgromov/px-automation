package configuration.helpers;

import org.apache.http.entity.ContentType;

import java.util.HashMap;
import java.util.Map;

import static config.Config.user;

/**
 * Created by kgr on 6/29/2017.
 */
public class SessionToken extends RequestHelper {
    private final String token;

    public SessionToken() {
        super();
        this.token = getSecurityToken();
        SessionsPool.registerSession(user, token);
    }

    public SessionToken(String token) {
        this.token = token;
        SessionsPool.registerSession(user, token);
    }

    @Override
    public Map<String, String> getSecurityHeaders(boolean useXMLContentType) {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer " + token);
        if (useXMLContentType) map.put("Accept", ContentType.APPLICATION_XML.getMimeType());
        return map;
    }

    @Override
    public Map<String, String> getSecurityHeaders() {
        return getSecurityHeaders(false);
    }

    String getToken() {
        return token;
    }
}
