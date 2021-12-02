package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.state.IState;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.Collections;
import java.util.Stack;

public interface IEpidemicRepository {
    
    IEpidemicRepository setState(IState state);
    
    IState getState();
    default void increase() {
        getState().getDiseaseRepository().nextInfectionMarker();
    }

    default void infect() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        for (int i = 0; i < 3; i++) {
            InfectionCard infectionCard = getState().getCardRepository().getInfectionDeck().pop();
            getState().getCardRepository().getInfectionDiscardPile().add(infectionCard);

            int amountCubes = infectionCard.getCity().getCubes().size();

            switch (amountCubes) {
                case 2 -> {
                    getState().getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    getState().getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                }
                case 3 -> getState().getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                default -> {
                    getState().getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    getState().getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    getState().getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                }
            }
        }
    }

    default void intensify() {
        Stack<InfectionCard> pile = getState().getCardRepository().getInfectionDiscardPile();
        Collections.shuffle(pile);

        getState().getCardRepository().getInfectionDeck().addAll(pile);
        getState().getCardRepository().setInfectionDiscardPile(new Stack<>());
    }

    void action() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;
}
