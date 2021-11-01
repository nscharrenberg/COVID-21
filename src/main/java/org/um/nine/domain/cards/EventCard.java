package org.um.nine.domain.cards;

import org.um.nine.contracts.repositories.IBoardRepository;

public abstract class EventCard extends PlayerCard {
    private String text;

    public EventCard(String name, String text) {
        super(name);
        this.text = text;
    }

    public abstract void event(IBoardRepository boardRepository);

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
