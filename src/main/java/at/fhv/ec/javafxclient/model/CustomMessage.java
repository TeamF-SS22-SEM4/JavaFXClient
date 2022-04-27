package at.fhv.ec.javafxclient.model;

public class CustomMessage {
    private final String jmsId;
    private final String title;
    private final String content;

    public CustomMessage(String jmsId, String title, String content) {
        this.jmsId = jmsId;
        this.title = title;
        this.content = content;
    }

    public String getJmsId() {
        return jmsId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
