package at.fhv.ec.javafxclient.model;

import java.util.Date;

public class CustomMessage {
    private final String jmsId;
    private final String title;
    private final String content;

    private final Date creationDate;

    public CustomMessage(String jmsId, String title, String content, Date creationDate) {
        this.jmsId = jmsId;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
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

    public Date getCreationDate() {
        return creationDate;
    }
}
