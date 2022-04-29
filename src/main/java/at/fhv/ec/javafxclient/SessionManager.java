package at.fhv.ec.javafxclient;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager session;
    private static String sessionId;

    private static String username;
    private static List<String> roles;
    private static List<String> topicNames;

    private static boolean newMessagesReceived;

    public static SessionManager getInstance() {
        if(session == null) {
            session = new SessionManager();
            sessionId = "";
            roles = new ArrayList<>();
            topicNames = new ArrayList<>();
            newMessagesReceived = false;
            username = "";
        }

        return session;
    }

    public void login(String aUsername, String aSessionId, List<String> aRolesList, List<String> aTopicNamesList) {
        username = aUsername;
        sessionId = aSessionId;
        roles.addAll(aRolesList);
        topicNames.addAll(aTopicNamesList);
    }

    public void logout() {
        sessionId = "";
        roles.clear();
        topicNames.clear();
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

    public List<String> getTopicNames() {
        return topicNames;
    }

    public boolean isNewMessagesReceived() {
        return newMessagesReceived;
    }
}