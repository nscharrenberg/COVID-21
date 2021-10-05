package org.um.nine.utils.cardmanaging;

import org.um.nine.domain.Card;
import org.um.nine.domain.City;
import org.um.nine.domain.Difficulty;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.EpidemicCard;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.cards.events.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Shuffle {

    /**
     * Shuffles the deck and then splitting the deck in the amount of difficulty.
     * Those get a epidemic card added and then shuffled again.
     * @return a shuffled deck of cards including difficulty*epidemic cards mixed in every quarter
     */
    public static Stack<PlayerCard> difficultyShuffle(Difficulty difficulty, Stack<PlayerCard> deck){
        Collections.shuffle(deck);
        Stack<PlayerCard> shuffled = new Stack<>();

        Stack<PlayerCard>[] split = new Stack[difficulty.getCount()];
        for(int i = 0; i < split.length; i++){
            split[i] = new Stack<>();
        }

        int counter = 0;
        for (PlayerCard c: deck) {
            if(counter > 3) counter = 0;
            split[counter].add(c);
            counter++;
        }


        for(int i = 0; i < split.length; i++){
            split[i].add(new EpidemicCard("Epidemic!"));
            Collections.shuffle(split[i]);
        }

        for(int i = 0; i < split.length; i++){
            shuffled.addAll(split[i]);
        }

        return shuffled;
    }


    public static Stack<PlayerCard> buildPlayerDeck(Difficulty difficultyLevel, HashMap<String,City> cities, HashMap<String, Player> players){
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
        Stack<Card> initialDeck = new Stack<>();
        for (int i = 0; i< k; i++){
            Card p = deck.pop();
            if (p instanceof CityCard){
                if (max_pop_cityCard== null || ((CityCard) p).getCity().getPopulation()>max_pop_cityCard.getCity().getPopulation()){
                    max_pop_cityCard  = ((CityCard) p);
                }
            }
            initialDeck.push(p);
        }
        for (Player p : players.values()) {
            for (int i = 0; i< amountCards; i++){
                PlayerCard card = (PlayerCard) initialDeck.pop();
                p.addCard(card);
                if (card.equals(max_pop_cityCard))
                    p.setItsTurn(true);
            }
        }

        return difficultyShuffle(difficultyLevel, deck);
    }

    public static Stack<Card> buildEpidemicDeck(HashMap<String,City> cities){
        Stack<Card> deck = new Stack<>();
        cities.values().stream().map(InfectionCard::new).forEach(deck::push);
        Collections.shuffle(deck);
        return deck;
    }

}
