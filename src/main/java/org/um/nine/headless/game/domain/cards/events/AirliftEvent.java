package org.um.nine.headless.game.domain.cards.events;

import org.um.nine.headless.game.domain.cards.EventCard;

public class AirliftEvent extends EventCard {
    public AirliftEvent() {
        super("Airlift Event", "action");
    }


    @Override
    public void event() {
        throw new UnsupportedOperationException("Not Supported");
    }
}
