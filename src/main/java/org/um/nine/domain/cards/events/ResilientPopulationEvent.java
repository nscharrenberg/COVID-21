package org.um.nine.domain.cards.events;

import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.screens.dialogs.ResilientPopulationDialog;

public class ResilientPopulationEvent extends EventCard {

    public ResilientPopulationEvent() {
        super("Resilient population event", "action");
    }

    @Override
    public void event(IBoardRepository boardRepository) {
        ResilientPopulationDialog resilientPopulationDialog = boardRepository.getResilientPopulationDialog();
        boardRepository.getGameRepository().getApp().getStateManager().attach(resilientPopulationDialog);
        resilientPopulationDialog.setEnabled(true);
        resilientPopulationDialog.setHeartbeat(true);
    }
}
