package org.um.nine.v1.domain.cards.events;

import org.um.nine.v1.domain.cards.EventCard;

public class GovernmentGrandEvent extends EventCard {
    public GovernmentGrandEvent() {
        super("Government Grand", "Add 1 research station to any city (no city card needed)");
    }

    @Override
    public void event() {
        // TODO: Add research station to the city that was selected by the player.
        // TODO: Move the card to the discard pile.
    }
}
