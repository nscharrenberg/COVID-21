package org.um.nine.domain.cards.events;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.screens.dialogs.DispatcherDialog;
import org.um.nine.screens.dialogs.PrognosisEventDialog;


public class PrognosisEvent extends EventCard {

    public PrognosisEvent() {
        super("Prognosis event", "Draw, look at, rearrange the top 6 cards of the infection deck. Put the m back on top");
    }

    @Override
    public void event(IBoardRepository boardRepository) {
        PrognosisEventDialog prognosisEventDialog = boardRepository.getPrognosisEventDialog();
        boardRepository.getGameRepository().getApp().getStateManager().attach(prognosisEventDialog);
        prognosisEventDialog.setEnabled(true);
        prognosisEventDialog.setHeartbeat(true);
        boardRepository.getCardRepository().getEventDiscardPile().add(this);
    }
}
