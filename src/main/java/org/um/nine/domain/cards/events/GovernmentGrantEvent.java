package org.um.nine.domain.cards.events;

import org.um.nine.domain.cards.EventCard;

public class GovernmentGrantEvent extends EventCard {
    public GovernmentGrantEvent() {
        super("Government Grand", "Add 1 research station to any city (no city card needed)");
    }

    @Override
    public void event() {
        // TODO: Add research station to the city that was selected by the player.
        // TODO: Move the card to the discard pile.
    }
}
