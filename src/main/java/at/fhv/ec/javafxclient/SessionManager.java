package at.fhv.ec.javafxclient;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager session;
    private static String sessionId;
    private static List<String> roles;

    public static SessionManager getInstance() {
        if(session == null) {
            session = new SessionManager();
            sessionId = "";
            roles = new ArrayList<>();
        }
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void login(String aSessionId, List<String> aRolesList) {
        sessionId = aSessionId;
        roles.addAll(aRolesList);
    }

    public void logout() {
        sessionId = "";
        roles.clear();
    }

}