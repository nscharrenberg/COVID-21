package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.Info;
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
import java.util.LinkedList;
import java.util.Stack;

public class CardRepository {
    private Stack<PlayerCard> playerDeck;
    private LinkedList<PlayerCard> eventDiscardPile;
    private Stack<InfectionCard> infectionDeck;
    private Stack<InfectionCard> infectionDiscardPile;

    public CardRepository() {
        reset();
    }

    public void reset() {
        this.playerDeck = new Stack<>();
        this.infectionDeck = new Stack<>();
        this.infectionDiscardPile = new Stack<>();
        this.eventDiscardPile = new LinkedList<>();
    }

    public void drawPlayerCard(PlayerCard... toDiscard) {
        PlayerCard drawn = this.playerDeck.pop();

        if (drawn instanceof EpidemicCard) {
            FactoryProvider.getEpidemicRepository().action();
            return;
        }

        Player currentPlayer = FactoryProvider.getPlayerRepository().getCurrentPlayer();

        if (currentPlayer.getHand().size() >= Info.HAND_LIMIT) {
            if (toDiscard.length <= 0) {
                // Just discard the currently picked card
                return;
            }

            PlayerCard discardCard = toDiscard[0];

            currentPlayer.addHand(drawn);
            currentPlayer.discard(discardCard);
        }
    }

    public void drawInfectionCard() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        InfectionCard infectionCard = this.infectionDeck.pop();

        FactoryProvider.getDiseaseRepository().infect(infectionCard.getCity().getColor(), infectionCard.getCity());
        this.infectionDiscardPile.push(infectionCard);
    }

    public void buildDecks() {
        this.playerDeck = CardUtils.buildPlayerDeck(FactoryProvider.getBoardRepository().getDifficulty(), FactoryProvider.getCityRepository().getCities(), FactoryProvider.getPlayerRepository().getPlayers());
        this.infectionDeck = CityUtils.generateInfectionDeck(FactoryProvider.getCityRepository().getCities().values().toArray(new City[0]));
        this.infectionDiscardPile = new Stack<>();
        Collections.shuffle(this.infectionDeck);

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
                        FactoryProvider.getDiseaseRepository().infect(d.getColor(), d.getCity());
                    } catch (NoDiseaseOrOutbreakPossibleDueToEvent | NoCubesLeftException | GameOverException noDiseaseOrOutbreakPossibleDueToEvent) {
                        noDiseaseOrOutbreakPossibleDueToEvent.printStackTrace();
                    }
                }
            }
        }
    }

    public Stack<PlayerCard> getPlayerDeck() {
        return playerDeck;
    }

    public void setPlayerDeck(Stack<PlayerCard> playerDeck) {
        this.playerDeck = playerDeck;
    }

    public LinkedList<PlayerCard> getEventDiscardPile() {
        return eventDiscardPile;
    }

    public void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile) {
        this.eventDiscardPile = eventDiscardPile;
    }

    public Stack<InfectionCard> getInfectionDeck() {
        return infectionDeck;
    }

    public void setInfectionDeck(Stack<InfectionCard> infectionDeck) {
        this.infectionDeck = infectionDeck;
    }

    public Stack<InfectionCard> getInfectionDiscardPile() {
        return infectionDiscardPile;
    }

    public void setInfectionDiscardPile(Stack<InfectionCard> infectionDiscardPile) {
        this.infectionDiscardPile = infectionDiscardPile;
    }
}
