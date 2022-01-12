package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.Collections;
import java.util.Stack;

import static org.um.nine.headless.game.Settings.DEFAULT_INITIAL_STATE;
import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;

public interface IEpidemicRepository {

    default void increase(IState state) {
        state.getDiseaseRepository().nextInfectionMarker();
    }

    default void infect(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        for (int i = 0; i < 3; i++) {
            InfectionCard infectionCard = state.getCardRepository().getInfectionDeck().pop();
            state.getCardRepository().getInfectionDiscardPile().add(infectionCard);

            int amountCubes = infectionCard.getCity().getCubes().size();

            switch (amountCubes) {
                case 2 -> {
                    state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                }
                case 3 -> state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                default -> {
                    state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                }
            }
        }
    }

    default void intensify(IState state) {
        Stack<InfectionCard> pile = state.getCardRepository().getInfectionDiscardPile();
        if (DEFAULT_INITIAL_STATE) Collections.shuffle(pile, RANDOM_PROVIDER);
        else Collections.shuffle(pile);

        state.getCardRepository().getInfectionDeck().addAll(pile);
        state.getCardRepository().setInfectionDiscardPile(new Stack<>());
    }

    void action(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;
}
