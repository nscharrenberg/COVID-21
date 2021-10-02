package org.um.nine.domain.cards;
import com.google.inject.Inject;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.repositories.local.BoardRepository;
import org.um.nine.repositories.local.CardRepository;
import org.um.nine.repositories.local.DiseaseRepository;
import org.um.nine.utils.cardmanaging.Shuffle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class EpidemicCard extends PlayerCard {

    @Inject
    private BoardRepository boardRepository;

    @Inject
    private CardRepository cardRepository;

    @Inject
    private DiseaseRepository diseaseRepository;

    public EpidemicCard(String name) {
        super(name);
    }

    public void increase() {
        boardRepository.getInfectionRateMarker().setCount(boardRepository.getInfectionRateMarker().getCount()+1);
    }

    public void infect() {
        for(int i = 0; i < 3; i++){
            InfectionCard infectionCard = cardRepository.getInfectionDeck().get(cardRepository.getInfectionDeck().size()-1);
            cardRepository.getInfectionDeck().remove(cardRepository.getInfectionDeck().size()-1);
            try{
                int amountCubes = infectionCard.getCity().getCubes().size();
                switch (amountCubes){
                    case 2:
                        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        break;
                    case 3:
                        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        break;
                    default:
                        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public void intensify() {
        Stack<InfectionCard> pile = cardRepository.getInfectionDiscardPile();
        Collections.shuffle(pile);
        cardRepository.getInfectionDeck().addAll(pile);
        cardRepository.setInfectionDiscardPile(new Stack<>());
    }

    public void action(){
        increase();
        infect();
        intensify();
    }
}
