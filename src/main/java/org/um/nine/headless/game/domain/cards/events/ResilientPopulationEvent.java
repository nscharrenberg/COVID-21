package org.um.nine.headless.game.domain.cards.events;

import org.um.nine.headless.game.domain.cards.EventCard;

public class ResilientPopulationEvent extends EventCard {
    public ResilientPopulationEvent() {
        super("Resilient population event", "action");
    }

    @Override
    public void event() {
        throw new UnsupportedOperationException("Not Supported");
    }
}
