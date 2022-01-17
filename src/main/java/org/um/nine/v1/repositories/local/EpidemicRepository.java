package org.um.nine.v1.repositories.local;

import com.google.inject.Inject;
import org.um.nine.v1.contracts.repositories.*;
import org.um.nine.v1.domain.cards.InfectionCard;

import java.util.Collections;
import java.util.Stack;

import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;

public class EpidemicRepository implements IEpidemicRepository {

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private ICardRepository cardRepository;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IDiseaseRepository diseaseRepository;

    private void increase() {
        diseaseRepository.nextInfectionMarker();
    }

    private void infect() {
        for(int i = 0; i < 3; i++){
            InfectionCard infectionCard = cardRepository.getInfectionDeck().get(0);
            cardRepository.getInfectionDeck().remove(0);
            cardRepository.getInfectionDiscardPile().add(infectionCard);
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

    private void intensify() {
        Stack<InfectionCard> pile = cardRepository.getInfectionDiscardPile();
        Collections.shuffle(pile, RANDOM_PROVIDER);
        cardRepository.getInfectionDeck().addAll(pile);
        cardRepository.setInfectionDiscardPile(new Stack<>());
    }

    @Override
    public void action() {
        increase();
        infect();
        intensify();
    }

}
