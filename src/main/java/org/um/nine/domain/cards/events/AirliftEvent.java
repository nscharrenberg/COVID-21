package org.um.nine.domain.cards.events;

import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.screens.dialogs.DispatcherDialog;

public class AirliftEvent extends EventCard {

    public AirliftEvent() {
        super("Air lift event", "Move any pawn to any city!");
    }

    @Override
    public void event(IBoardRepository boardRepository) {
        DispatcherDialog dispatcherDialog = boardRepository.getDispatcherDialog();
        boardRepository.getGameRepository().getApp().getStateManager().attach(dispatcherDialog);
        dispatcherDialog.setEnabled(true);
        dispatcherDialog.setHeartbeat(true);
        boardRepository.getCardRepository().getEventDiscardPile().add(this);
    }
}
