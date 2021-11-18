package org.um.nine.headless.game.domain.cards.events;

import org.um.nine.headless.game.domain.cards.EventCard;

public class QuietNightEvent extends EventCard {
    public QuietNightEvent() {
        super("Quiet night event", "action");
    }

    @Override
    public void event() {
        throw new UnsupportedOperationException("Not Supported");
    }
}
