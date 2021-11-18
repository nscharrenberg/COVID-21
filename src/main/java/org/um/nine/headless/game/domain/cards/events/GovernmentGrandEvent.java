package org.um.nine.headless.game.domain.cards.events;

import org.um.nine.headless.game.domain.cards.EventCard;

public class GovernmentGrandEvent extends EventCard {
    public GovernmentGrandEvent() {
        super("Government Grand", "Add 1 research station to any city (no city card needed)");
    }

    @Override
    public void event() {
        throw new UnsupportedOperationException("Not Supported");
    }
}
