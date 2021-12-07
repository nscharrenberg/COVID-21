package org.um.nine.headless.game.agents;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.contracts.repositories.ICardRepository;
import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.HashMap;
import java.util.Stack;

public class state{
    private String action;
    private HashMap<String, City> cities;
    private HashMap<String, Player> players;
    private Stack<PlayerCard> playerDeck;
    private Stack<InfectionCard> infectionDeck;
    private Stack<InfectionCard> infectionDiscardPile;
    private String currentPlayerName;
    private City target;


    state(String action, City targetLocation){
        this.target = targetLocation;
        this.action = action;
        this.currentPlayerName = FactoryProvider.getPlayerRepository().getCurrentPlayer().getName();
        this.cities = (HashMap<String, City>) FactoryProvider.getCityRepository().getCities().clone();
        this.playerDeck = (Stack<PlayerCard>) FactoryProvider.getCardRepository().getPlayerDeck().clone();
        this.infectionDeck = (Stack<InfectionCard>) FactoryProvider.getCardRepository().getInfectionDeck().clone();
        this.infectionDiscardPile = (Stack<InfectionCard>) FactoryProvider.getCardRepository().getInfectionDiscardPile().clone();
        this.players = (HashMap<String, Player>) FactoryProvider.getPlayerRepository().getPlayers().clone();
    }

    @Override
    public String toString() {
        return currentPlayerName + action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Stack<InfectionCard> getInfectionDiscardPile() {
        return infectionDiscardPile;
    }

    public Stack<InfectionCard> getInfectionDeck() {
        return infectionDeck;
    }

    public Stack<PlayerCard> getPlayerDeck() {
        return playerDeck;
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public HashMap<String, City> getCities() {
        return cities;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public City getTarget() {
        return target;
    }
}