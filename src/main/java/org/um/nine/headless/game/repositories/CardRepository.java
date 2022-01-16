package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.contracts.repositories.ICardRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.EpidemicCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.exceptions.GameOverException;
import org.um.nine.headless.game.exceptions.NoCubesLeftException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.headless.game.utils.CardUtils;
import org.um.nine.headless.game.utils.CityUtils;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

import static org.um.nine.headless.game.Settings.*;

public class CardRepository implements ICardRepository {
    private Stack<PlayerCard> playerDeck;
    private LinkedList<PlayerCard> eventDiscardPile;
    private Stack<InfectionCard> infectionDeck;
    private Stack<InfectionCard> infectionDiscardPile;

    public CardRepository() {
    }

    @Override
    public CardRepository clone() {
        try {
            CardRepository clone = (CardRepository) super.clone();
            clone.setPlayerDeck(new CardUtils.StackCloner<PlayerCard>().cloneStack(this.playerDeck));
            clone.setEventDiscardPile(new LinkedList<>());
            Collections.copy(this.eventDiscardPile, clone.getEventDiscardPile());
            clone.setInfectionDeck(new CardUtils.StackCloner<InfectionCard>().cloneStack(this.infectionDeck));
            clone.setInfectionDiscardPile(new CardUtils.StackCloner<InfectionCard>().cloneStack(this.infectionDiscardPile));
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reset the card states back to its original empty values
     */
    @Override
    public void reset() {
        this.playerDeck = new Stack<>();
        this.infectionDeck = new Stack<>();
        this.infectionDiscardPile = new Stack<>();
        this.eventDiscardPile = new LinkedList<>();
    }

    /**
     * Draw a player card and give it to the current player
     * When the hand limit is reached one of the cards must be discarded.
     *
     * @param toDiscard - The card to discard when exceeding the limit,
     *                  if nothing is given then the card just drawn will be discarded
     */
    @Override
    public void drawPlayerCard(IState state, PlayerCard... toDiscard) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        PlayerCard drawn;
        try {
            drawn = this.playerDeck.pop();
            if (LOG && DEFAULT_RUNNING_GAME != null)
                DEFAULT_RUNNING_GAME.append("Drawing player card " + drawn.getName());
        } catch (EmptyStackException e) {
            throw new GameOverException();
        }


        if (drawn instanceof EpidemicCard) {
            state.getEpidemicRepository().action(state);
            return;
        }

        Player currentPlayer = state.getPlayerRepository().getCurrentPlayer();

        if (currentPlayer.getHand().size() >= Settings.HAND_LIMIT) {
            if (toDiscard.length <= 0) {
                // Just discard the currently picked card
                return;
            }

            PlayerCard discardCard = toDiscard[0];

            currentPlayer.addHand(drawn);
            currentPlayer.discard(discardCard);
        } else {
            currentPlayer.addHand(drawn);
        }
    }

    /**
     * Draw an infection Card and infect cities
     *
     * @throws NoCubesLeftException                  - Thrown when no cubes of the correlating infection card are left.
     * @throws NoDiseaseOrOutbreakPossibleDueToEvent - Thrown when a cube can't be place due to an event
     * @throws GameOverException                     - Thrown when the player lost the game
     */
    @Override
    public void drawInfectionCard(IState state) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        InfectionCard infectionCard = this.infectionDeck.pop();
        if (LOG && DEFAULT_RUNNING_GAME != null)
            DEFAULT_RUNNING_GAME.append("Drawing infection card " + infectionCard.getName());
        state.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
        this.infectionDiscardPile.push(infectionCard);
    }

    /**
     * Build the Deck by giving players their cards, and infecting cities (initial setup)
     */
    @Override
    public void buildDecks(IState state) {
        if (DEFAULT_INITIAL_STATE) {
            this.playerDeck = CardUtils.loadPlayerDeck(state.getCityRepository().getCities(), state.getPlayerRepository().getPlayers(), state.getPlayerRepository());
            this.infectionDeck = CityUtils.loadInfectionDeck(state.getCityRepository().getCities());
        } else {
            this.playerDeck = CardUtils.buildPlayerDeck(state.getPlayerRepository(), state.getBoardRepository().getDifficulty(), state.getCityRepository().getCities(), state.getPlayerRepository().getPlayers());
            this.infectionDeck = CityUtils.generateInfectionDeck(state.getCityRepository().getCities().values().toArray(new City[0]));
            Collections.shuffle(this.infectionDeck);
        }


        this.infectionDiscardPile = new Stack<>();


        // Set initial infection:
        // draw 3 cards 3 cubes, 3 cards 2 cubes, 3 cards 1 cube
        // and place cards on infection discard pile
        for (int i = 3; i > 0; i--) {
            for (int j = 0; j < 3; j++) {
                InfectionCard c = this.infectionDeck.pop();
                infectionDiscardPile.add(c);

                Disease d = new Disease(c.getCity().getColor());
                for (int k = i; k > 0; k--) {
                    try {
                        state.getDiseaseRepository().infect(d.getColor(), c.getCity());
                    } catch (NoCubesLeftException | GameOverException noDiseaseOrOutbreakPossibleDueToEvent) {
                        noDiseaseOrOutbreakPossibleDueToEvent.printStackTrace();
                    } catch (NoDiseaseOrOutbreakPossibleDueToEvent noDiseaseOrOutbreakPossibleDueToEvent) {
                        System.err.println("Prevented a disease from being added or outbreak from happening in " +
                                noDiseaseOrOutbreakPossibleDueToEvent.getCity());
                    }
                }
            }
        }
    }

    @Override
    public Stack<PlayerCard> getPlayerDeck() {
        return playerDeck;
    }

    @Override
    public void setPlayerDeck(Stack<PlayerCard> playerDeck) {
        this.playerDeck = playerDeck;
    }

    @Override
    public LinkedList<PlayerCard> getEventDiscardPile() {
        return eventDiscardPile;
    }

    @Override
    public void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile) {
        this.eventDiscardPile = eventDiscardPile;
    }

    @Override
    public Stack<InfectionCard> getInfectionDeck() {
        return infectionDeck;
    }

    @Override
    public void setInfectionDeck(Stack<InfectionCard> infectionDeck) {
        this.infectionDeck = infectionDeck;
    }

    @Override
    public Stack<InfectionCard> getInfectionDiscardPile() {
        return infectionDiscardPile;
    }

    @Override
    public void setInfectionDiscardPile(Stack<InfectionCard> infectionDiscardPile) {
        this.infectionDiscardPile = infectionDiscardPile;
    }
}

