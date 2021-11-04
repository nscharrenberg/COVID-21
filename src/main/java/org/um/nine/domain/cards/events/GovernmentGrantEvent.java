package org.um.nine.domain.cards.events;

import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.screens.dialogs.GovernmentGrantDialog;

public class GovernmentGrantEvent extends EventCard {
    public GovernmentGrantEvent() {
        super("Government Grand", "Add 1 research station to any city (no city card needed)");
    }

    @Override
    public void event(IBoardRepository boardRepository) {
        GovernmentGrantDialog governmentGrantDialog = boardRepository.getGovernmentGrantDialog();
        boardRepository.getGameRepository().getApp().getStateManager().attach(governmentGrantDialog);
        governmentGrantDialog.setEnabled(true);
        governmentGrantDialog.setHeartbeat(true);
        boardRepository.getCardRepository().getEventDiscardPile().add(this);

        // TODO: Add research station to the city that was selected by the player.
        // TODO: Move the card to the discard pile.
    }
}
