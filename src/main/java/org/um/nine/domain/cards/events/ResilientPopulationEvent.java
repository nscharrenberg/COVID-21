package org.um.nine.domain.cards.events;

import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.domain.cards.EventCard;

public class ResilientPopulationEvent extends EventCard {

    public ResilientPopulationEvent() {
        super("Resilient population event", "action");
    }

    @Override
    public void event(IBoardRepository boardRepository) {

    }
}
