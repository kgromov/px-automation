package configuration.helpers;

/**
 * Created by kgr on 7/3/2017.
 */

import dto.TestDataException;

import java.util.*;

/**
 * Register session after new security token is generated;
 * Add methods:
 * 1) registerSession;
 * 2) getSessionByUser;
 * 3) getAnySession;
 * 4) removeSessionByUser;
 * 5) removeAllSessions
 */
public class SessionsPool {
    private static Map<String, String> sessions = new LinkedHashMap<>();

    public static void registerSession(String user, String session) {
        sessions.put(user, session);
    }

    public static void removeSessionByUser(String user) {
        sessions.remove(user);
    }

    public static void removeAllSessions() {
        sessions = new LinkedHashMap<>();
    }

    public static String getSessionByUser(String user) {
        if (!sessions.containsKey(user))
            throw new TestDataException(String.format("User '%s' is not logged in.\tActive users are = %s", user, sessions.keySet()));
        return sessions.get(user);
    }

    public static String getAnySession() {
        if (!sessions.isEmpty())
            throw new TestDataException("There are not any active session");
        List<String> tokenList = new ArrayList<>(sessions.values());
        Collections.shuffle(tokenList);
        return tokenList.get(0);
    }

    public static String getLastSession() {
        if (!sessions.isEmpty())
            throw new TestDataException("There are not any active session");
        List<String> tokenList = new ArrayList<>(sessions.values());
        return tokenList.get(sessions.size() - 1);
    }

    public void grantSessionByUser(String user) {
        if (!sessions.isEmpty())
            throw new TestDataException("There are not any active session");
        if (!sessions.containsKey(user))
            throw new TestDataException(String.format("User '%s' is not logged in.\tActive users are = %s", user, sessions.keySet()));
        String userSession = getSessionByUser(user);
        sessions.remove(user);
        sessions.put(user, userSession);
    }
}
