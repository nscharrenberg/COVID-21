package org.um.nine.headless.game.utils;

import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.EpidemicCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.cards.events.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

public class CardUtils {
    public static Stack<PlayerCard> shuffle(Difficulty difficulty, Stack<PlayerCard> deck) {
        Collections.shuffle(deck);
        Stack<PlayerCard> shuffled = new Stack<>();

        Stack<PlayerCard>[] split = new Stack[difficulty.getCount()];

        for(int i = 0; i < split.length; i++){
            split[i] = new Stack<>();
        }

        int counter = 0;

        for (PlayerCard c: deck) {
            if(counter > difficulty.getCount()-1) counter = 0;
            split[counter].add(c);
            counter++;
        }

        for (Stack<PlayerCard> playerCards : split) {
            playerCards.add(new EpidemicCard("Epidemic!"));
            Collections.shuffle(playerCards);
        }

        for (Stack<PlayerCard> playerCards : split) {
            shuffled.addAll(playerCards);
        }

        return shuffled;
    }

    public static Stack<PlayerCard> buildPlayerDeck(IPlayerRepository playerRepository, Difficulty difficulty, HashMap<String, City> cities, HashMap<String, Player> players) {
        Stack<PlayerCard> deck = new Stack<>();
        deck.push(new GovernmentGrandEvent());
        deck.push(new PrognosisEvent());
        deck.push(new AirliftEvent());
        deck.push(new QuietNightEvent());
        deck.push(new ResilientPopulationEvent());
        cities.values().stream().map(CityCard::new).forEach(deck::push);

        Collections.shuffle(deck);
        int amountCards = switch (players.values().size()) {
            case 2 -> 4;
            case 3 -> 3;
            case 4 -> 2;
            default -> 0;
        };

        int k = amountCards*players.size();
        CityCard max_pop_cityCard = null;
        Stack<PlayerCard> initialDeck = new Stack<>();
        for (int i = 0; i< k; i++){
            PlayerCard p = deck.pop();
            if (p instanceof CityCard){
                if (max_pop_cityCard== null || ((CityCard) p).getCity().getPopulation()>max_pop_cityCard.getCity().getPopulation()){
                    max_pop_cityCard  = ((CityCard) p);
                }
            }
            initialDeck.push(p);
        }
        for (Player p : players.values()) {
            for (int i = 0; i< amountCards; i++){
                PlayerCard card = initialDeck.pop();
                p.addHand(card);
                if (card.equals(max_pop_cityCard)) {
                    playerRepository.setCurrentPlayer(p);
                }
            }
        }

        return shuffle(difficulty, deck);
    }
}
