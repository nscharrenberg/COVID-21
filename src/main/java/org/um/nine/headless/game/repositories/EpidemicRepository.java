package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.Collections;
import java.util.Stack;

public class EpidemicRepository {
    private void increase() {
        FactoryProvider.getDiseaseRepository().nextInfectionMarker();
    }

    private void infect() {
        for (int i = 0; i < 3; i++) {
            InfectionCard infectionCard = FactoryProvider.getCardRepository().getInfectionDeck().pop();
            FactoryProvider.getCardRepository().getInfectionDiscardPile().add(infectionCard);

            int amountCubes = infectionCard.getCity().getCubes().size();

            try {
                switch (amountCubes) {
                    case 2 -> {
                        FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    }
                    case 3 -> FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    default -> {
                        FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                        FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
                    }
                }
            } catch (NoCubesLeftException | NoDiseaseOrOutbreakPossibleDueToEvent | GameOverException e) {
                e.printStackTrace();
            }
        }
    }

    private void intensify() {
        Stack<InfectionCard> pile = FactoryProvider.getCardRepository().getInfectionDiscardPile();
        Collections.shuffle(pile);

        FactoryProvider.getCardRepository().getInfectionDeck().addAll(pile);
        FactoryProvider.getCardRepository().setInfectionDiscardPile(new Stack<>());
    }

    public void action() {
        increase();
        infect();
        intensify();
    }
}
