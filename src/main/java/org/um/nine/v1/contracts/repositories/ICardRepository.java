package org.um.nine.v1.contracts.repositories;

import org.um.nine.v1.domain.cards.InfectionCard;
import org.um.nine.v1.domain.cards.PlayerCard;
import org.um.nine.v1.exceptions.GameOverException;
import org.um.nine.v1.exceptions.NoCubesLeftException;
import org.um.nine.v1.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.v1.exceptions.OutbreakException;

import java.util.LinkedList;
import java.util.Stack;

public interface ICardRepository {
    Stack<PlayerCard> getPlayerDeck();
    Stack<InfectionCard> getInfectionDeck();

    Stack<InfectionCard> getInfectionDiscardPile();

    void setInfectionDiscardPile(Stack<InfectionCard> newPile);

    void reset();

    void drawPlayCard();

    void drawInfectionCard() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;

    void buildDecks() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException, OutbreakException;

    LinkedList<PlayerCard> getEventDiscardPile();

    void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile);

    void cleanup();
}
