package org.um.nine.domain.cards.events;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.screens.dialogs.DispatcherDialog;

public class AirliftEvent extends EventCard {

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private DispatcherDialog dispatcherDialog;

    @Inject
    private ICardRepository cardRepository;

    public AirliftEvent() {
        super("Air lift event", "Move any pawn to any city!");
    }

    @Override
    public void event() {
        gameRepository.getApp().getStateManager().attach(dispatcherDialog);
        dispatcherDialog.setEnabled(true);
        dispatcherDialog.setHeartbeat(true);
        cardRepository.getEventDiscardPile().add(this);
    }
}
