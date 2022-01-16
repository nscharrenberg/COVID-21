package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.LinkedList;
import java.util.Stack;

public interface ICardRepository extends Cloneable {
    ICardRepository clone();

    void reset();

    void drawPlayerCard(IState state, PlayerCard... toDiscard) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;

    void drawInfectionCard(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;

    void buildDecks(IState state);

    Stack<PlayerCard> getPlayerDeck();

    void setPlayerDeck(Stack<PlayerCard> playerDeck);

    LinkedList<PlayerCard> getEventDiscardPile();

    void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile);

    Stack<InfectionCard> getInfectionDeck();

    void setInfectionDeck(Stack<InfectionCard> infectionDeck);

    Stack<InfectionCard> getInfectionDiscardPile();

    void setInfectionDiscardPile(Stack<InfectionCard> infectionDiscardPile);
}
