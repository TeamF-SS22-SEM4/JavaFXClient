package at.fhv.ec.javafxclient;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager session;
    private static String sessionId;

    private static String username;
    private static List<String> roles;

    private static boolean newMessagesReceived;

    public static SessionManager getInstance() {
        if(session == null) {
            session = new SessionManager();
            sessionId = "";
            roles = new ArrayList<>();
            newMessagesReceived = false;
            username = "";
        }

        return session;
    }

    public void login(String aUsername, String aSessionId, List<String> aRolesList) {
        username = aUsername;
        sessionId = aSessionId;
        roles.addAll(aRolesList);
    }

    public void logout() {
        sessionId = "";
        roles.clear();
        newMessagesReceived = false;
    }

    public void onNewMessageReceived() {
        newMessagesReceived = true;
    }

    public void onMessageViewed() {
        newMessagesReceived = false;
    }

    public String getUsername() {
        return username;
    }
    public String getSessionId() {
        return sessionId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isNewMessagesReceived() {
        return newMessagesReceived;
    }
}