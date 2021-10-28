package org.um.nine.domain.cards.events;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.exceptions.GameOverException;
import org.um.nine.screens.dialogs.PrognosisEventDialog;

import java.util.ArrayList;

public class PrognosisEvent extends EventCard {

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private PrognosisEventDialog prognosisEventDialog;

    public PrognosisEvent() {
        super("Prognosis event", "Draw, look at, rearrange the top 6 cards of the infection deck. Put the m back on top");
    }

    @Inject
    private ICardRepository cardRepository;

    @Override
    public void event() {
        gameRepository.getApp().getStateManager().attach(prognosisEventDialog);
        prognosisEventDialog.setEnabled(true);
        prognosisEventDialog.setHeartbeat(true);
    }
}
