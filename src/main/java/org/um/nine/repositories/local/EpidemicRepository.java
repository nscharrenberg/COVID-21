package org.um.nine.repositories.local;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.Marker;
import org.um.nine.domain.OutbreakMarker;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.exceptions.GameOverException;
import org.um.nine.screens.dialogs.DialogBoxState;

import java.util.Collections;
import java.util.Stack;

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
        Collections.shuffle(pile);
        cardRepository.getInfectionDeck().addAll(pile);
        cardRepository.setInfectionDiscardPile(new Stack<>());
    }

    @Override
    public void action(){
        DialogBoxState dialog = new DialogBoxState("An outbreak has occurred");
        gameRepository.getApp().getStateManager().attach(dialog);
        dialog.setEnabled(true);

        increase();
        infect();
        intensify();
    }

}
