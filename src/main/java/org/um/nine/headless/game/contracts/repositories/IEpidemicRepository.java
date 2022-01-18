package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.um.nine.headless.game.Settings.DEFAULT_INITIAL_STATE;
import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;

public interface IEpidemicRepository extends Cloneable {

    IEpidemicRepository clone();

    default void increase(IState state) {
        state.getDiseaseRepository().nextInfectionMarker();
    }

    default void infect(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {

        InfectionCard infectionCard = state.getCardRepository().getInfectionDeck().get(state.getCardRepository().getInfectionDeck().size() - 1);
        state.getCardRepository().getInfectionDeck().remove(infectionCard);
        state.getCardRepository().getInfectionDiscardPile().push(infectionCard);

        if (state.getDiseaseRepository().getCures().get(infectionCard.getCity().getColor()).isDiscovered()) return;

        int diseasesInCity = (int) infectionCard.getCity().getCubes().stream().filter(disease -> disease.getColor().equals(infectionCard.getCity().getColor())).count();

        if (diseasesInCity == 0) {
            //just place 3 cubes
            for (int i = 0; i < 3; i++)
                state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity(), state);
        } else {
            //place as many cubes as needed to get to 3
            int amountCubes = 3 - diseasesInCity;
            for (int i = 0; i < amountCubes; i++)
                state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity(), state);
            //init an outbreak
            state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity(), state);
        }


    }

    default void intensify(IState state) {
        Stack<InfectionCard> pile = state.getCardRepository().getInfectionDiscardPile();
        List<InfectionCard> shuffled = new ArrayList<>();
        while (!pile.isEmpty()) {
            shuffled.add(pile.pop());
        }

        if (DEFAULT_INITIAL_STATE) Collections.shuffle(shuffled, RANDOM_PROVIDER);
        else Collections.shuffle(shuffled);
        shuffled.forEach(ic -> state.getCardRepository().getInfectionDeck().push(ic));

        state.getCardRepository().setInfectionDiscardPile(new Stack<>());
    }

    void action(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;
}
