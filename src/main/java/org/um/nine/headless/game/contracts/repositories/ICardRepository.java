package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.LinkedList;
import java.util.Stack;

public interface ICardRepository {
    void reset();

    void drawPlayerCard(PlayerCard... toDiscard);

    void drawInfectionCard() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;

    void buildDecks();

    Stack<PlayerCard> getPlayerDeck();

    void setPlayerDeck(Stack<PlayerCard> playerDeck);

    LinkedList<PlayerCard> getEventDiscardPile();

    void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile);

    Stack<InfectionCard> getInfectionDeck();

    void setInfectionDeck(Stack<InfectionCard> infectionDeck);

    Stack<InfectionCard> getInfectionDiscardPile();

    void setInfectionDiscardPile(Stack<InfectionCard> infectionDiscardPile);
}
