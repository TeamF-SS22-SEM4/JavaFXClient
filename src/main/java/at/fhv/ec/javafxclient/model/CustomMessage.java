package at.fhv.ec.javafxclient.model;


import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomMessage {
    private final String jmsId;
    private final String title;
    private final String content;

    private final String creationDate;

    public CustomMessage(String jmsId, String title, String content, Date creationDate) {
        this.jmsId = jmsId;
        this.title = title;
        this.content = content;
        this.creationDate = new SimpleDateFormat("hh:mm dd-MM-yyyy").format(creationDate);
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

    public String getCreationDate() {
        return creationDate;
    }
}
