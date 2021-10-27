package org.um.nine.domain.cards.events;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.exceptions.GameOverException;

import java.util.ArrayList;

public class PrognosisEvent extends EventCard {
    public PrognosisEvent() {
        super("Prognosis event", "Draw, look at, rearrange the top 6 cards of the infection deck. Put the m back on top");
    }

    @Inject
    private ICardRepository cardRepository;

    @Override
    public void event() {
        ArrayList<InfectionCard> topCards = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            try{
                topCards.add(cardRepository.getInfectionDeck().pop());
            } catch (Exception e){
                topCards.forEach(c -> cardRepository.getInfectionDeck().add(c));
                e.printStackTrace();
            }
        }
    }
}
