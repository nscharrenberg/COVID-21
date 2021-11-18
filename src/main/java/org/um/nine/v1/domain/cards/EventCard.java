package org.um.nine.v1.domain.cards;

public abstract class EventCard extends PlayerCard {
    private String text;

    public EventCard(String name, String text) {
        super(name);
        this.text = text;
    }

    public abstract void event();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
