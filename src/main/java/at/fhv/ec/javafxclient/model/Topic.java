package at.fhv.ec.javafxclient.model;

public class Topic {
    private final String name;
    private final int amountOfMessages;

    public Topic(String name) {
        this.name = name;
        this.amountOfMessages = 0;
    }

    public Topic(String name, int amountOfMessages) {
        this.name = name;
        this.amountOfMessages = amountOfMessages;
    }

    public String getName() {
        return name;
    }

    public int getAmountOfMessages() {
        return amountOfMessages;
    }
}
