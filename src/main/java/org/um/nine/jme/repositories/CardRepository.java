package org.um.nine.jme.repositories;

import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.headless.agents.state.GameStateFactory;

import java.util.LinkedList;
import java.util.Stack;

public class CardRepository {

    public CardRepository() {
    }

    /**
     * Reset the card states back to its original empty values
     */

    public void reset() {
        GameStateFactory.getInitialState().getCardRepository().reset();
    }

    /**
     * Draw a player card and give it to the current player
     * When the hand limit is reached one of the cards must be discarded.
     * 
     * @param toDiscard - The card to discard when exceeding the limit,
     *                  if nothing is given then the card just drawn will be
     *                  discarded
     */

    public void drawPlayerCard(PlayerCard... toDiscard)
            throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        GameStateFactory.getInitialState().getCardRepository().drawPlayerCard(toDiscard);
    }

    /**
     * Draw an infection Card and infect cities
     * 
     * @throws NoCubesLeftException                  - Thrown when no cubes of the
     *                                               correlating infection card are
     *                                               left.
     * @throws NoDiseaseOrOutbreakPossibleDueToEvent - Thrown when a cube can't be
     *                                               place due to an event
     * @throws GameOverException                     - Thrown when the player lost
     *                                               the game
     */

    public void drawInfectionCard()
            throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {

        GameStateFactory.getInitialState().getCardRepository().drawInfectionCard();
    }

    /**
     * Build the Deck by giving players their cards, and infecting cities (initial
     * setup)
     */

    public void buildDecks() {
        GameStateFactory.getInitialState().getCardRepository().buildDecks();
    }

    public Stack<PlayerCard> getPlayerDeck() {
        return GameStateFactory.getInitialState().getCardRepository().getPlayerDeck();
    }

    public void setPlayerDeck(Stack<PlayerCard> playerDeck) {
        GameStateFactory.getInitialState().getCardRepository().setPlayerDeck(playerDeck);
    }

    public LinkedList<PlayerCard> getEventDiscardPile() {
        return GameStateFactory.getInitialState().getCardRepository().getEventDiscardPile();
    }

    public void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile) {
        GameStateFactory.getInitialState().getCardRepository().setEventDiscardPile(eventDiscardPile);
    }

    public Stack<InfectionCard> getInfectionDeck() {
        return GameStateFactory.getInitialState().getCardRepository().getInfectionDeck();
    }

    public void setInfectionDeck(Stack<InfectionCard> infectionDeck) {
        GameStateFactory.getInitialState().getCardRepository().setInfectionDeck(infectionDeck);
    }

    public Stack<InfectionCard> getInfectionDiscardPile() {
        return GameStateFactory.getInitialState().getCardRepository().getInfectionDiscardPile();
    }

    public void setInfectionDiscardPile(Stack<InfectionCard> infectionDiscardPile) {
        GameStateFactory.getInitialState().getCardRepository().setInfectionDiscardPile(infectionDiscardPile);
    }
}
