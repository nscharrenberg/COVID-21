package org.um.nine.headless.game.domain.cards.events;

import org.um.nine.headless.game.domain.cards.EventCard;

public class PrognosisEvent extends EventCard {
    public PrognosisEvent() {
        super("Prognosis event", "action");
    }


    @Override
    public void event() {
        throw new UnsupportedOperationException("Not Supported");
    }
}
