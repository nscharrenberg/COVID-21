package org.um.nine.headless.game.agents;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.contracts.repositories.ICardRepository;
import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Log {
    private LinkedList<state> log;

    public Log(){
        log = new LinkedList<>();
    }

    public void addStep(String action){
        log.add(new state(action, FactoryProvider.getCityRepository().getCities(),FactoryProvider.getCardRepository(), FactoryProvider.getPlayerRepository()));
    }

    public LinkedList<state> getLog() {
        return log;
    }

    class state{
        private String action;
        private HashMap<String, City> cities;
        private HashMap<String, Player> players;
        private Stack<PlayerCard> playerDeck;
        private Stack<InfectionCard> infectionDeck;
        private Stack<InfectionCard> infectionDiscardPile;
        private Player currentPlayer;

        state(String action, HashMap<String, City> cities, ICardRepository cards, IPlayerRepository players){
            this.action = action;
            this.currentPlayer = players.getCurrentPlayer();
            this.cities = (HashMap<String, City>) cities.clone();
            this.playerDeck = (Stack<PlayerCard>) cards.getPlayerDeck().clone();
            this.infectionDeck = (Stack<InfectionCard>) cards.getInfectionDeck().clone();
            this.infectionDiscardPile = (Stack<InfectionCard>) cards.getInfectionDiscardPile().clone();
            this.players = (HashMap<String, Player>) players.getPlayers().clone();
        }

        @Override
        public String toString() {

            StringBuilder playerInfo = new StringBuilder();
            players.values().forEach(p -> {
                playerInfo.append(p.getName() + " is at " + p.getCity().getName() + " and has those cards: " + p.getHand() + "\n");
            });

            StringBuilder cityInfo = new StringBuilder();
            cities.values().forEach(c -> {
                if(c.getCubes().size() > 0){
                    cityInfo.append(c.getName() + " has " + c.getCubes().size() + " Disease cubes. \n");
                }
            });
            return "State: \n" +
                    currentPlayer.getName() + " executed " + action + "\n" +
                    playerInfo +
                    cityInfo +
                    "Remaining Cards: " + playerDeck + "\n" +
                    "Infection Deck: "  + infectionDeck + "\n" +
                    "Infection Discard Pile: " + infectionDiscardPile;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
