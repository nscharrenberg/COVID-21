package org.um.nine.contracts.repositories;

import org.um.nine.domain.Card;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.exceptions.GameOverException;
import org.um.nine.exceptions.NoCubesLeftException;
import org.um.nine.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.exceptions.OutbreakException;

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
}
