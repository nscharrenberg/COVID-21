package org.um.nine.domain.cards.events;

import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.domain.cards.EventCard;

public class QuietNightEvent extends EventCard {

    public QuietNightEvent() {
        super("Quiet night event", "action");
    }

    @Override
    public void event(IBoardRepository boardRepository) {

    }
}
