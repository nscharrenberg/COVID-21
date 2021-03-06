package org.um.nine.headless.game.utils;

import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.EpidemicCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;

public class CardUtils {
    public static List<PlayerCard> cloneOf(List<PlayerCard> cards) {
        return cards.stream().map(PlayerCard::clone).collect(Collectors.toList());
    }

    public static Stack<PlayerCard> shuffle(Difficulty difficulty, Stack<PlayerCard> deck) {
        Collections.shuffle(deck, RANDOM_PROVIDER);
        Stack<PlayerCard> shuffled = new Stack<>();

        Stack<PlayerCard>[] split = new Stack[difficulty.getCount()];

        for (int i = 0; i < split.length; i++) {
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
            Collections.shuffle(playerCards, RANDOM_PROVIDER);
        }

        for (Stack<PlayerCard> playerCards : split) {
            shuffled.addAll(playerCards);
        }

        return shuffled;
    }

    public static Stack<PlayerCard> loadPlayerDeck(HashMap<String,City> cities, HashMap<String,Player> players, IPlayerRepository playerRepository) {
        Stack<PlayerCard> playerDeck = new Stack<>();
        City max_pop_city = null;
        try {
            FileReader fr = new FileReader(
                    Objects.requireNonNull(
                            CityUtils.class.getClassLoader().getResource(
                                    "Cards/PlayerDeck.txt")
                    ).getFile()
            );
            Scanner sc = new Scanner(fr);
            while(sc.hasNextLine()){
                String s = sc.nextLine();
                if (cities.get(s)!= null){
                    playerDeck.push(new CityCard(cities.get(s)));
                    if (max_pop_city== null || cities.get(s).getPopulation() > max_pop_city.getPopulation()){
                        max_pop_city  = cities.get(s);
                    }
                }
                else if (s.equals("Epidemic")){
                    playerDeck.push(new EpidemicCard(s));
                }
                else System.out.println(s);
            }
            sc.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Player p : players.values()) {
            for (int i = 0; i< 2; i++){
                PlayerCard card = playerDeck.pop();
                p.addHand(card);
                if (card instanceof CityCard cc && cc.getCity().equals(max_pop_city)) {
                    playerRepository.setCurrentPlayer(p);
                }
            }
        }
        return playerDeck;
    }

    public static Stack<PlayerCard> buildPlayerDeck(IPlayerRepository playerRepository, Difficulty difficulty, HashMap<String, City> cities, HashMap<String, Player> players) {
        Stack<PlayerCard> deck = new Stack<>();
//        deck.push(new GovernmentGrandEvent());
//        deck.push(new PrognosisEvent());
//        deck.push(new AirliftEvent());
//        deck.push(new QuietNightEvent());
//        deck.push(new ResilientPopulationEvent());
        cities.values().stream().map(CityCard::new).forEach(deck::push);

        Collections.shuffle(deck, RANDOM_PROVIDER);
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

    public static class StackCloner<T extends Card> {
        public Stack<T> cloneStack(Stack<T> stack) {
            Stack<T> reversed = new Stack<>(), clone = new Stack<>();
            while (!stack.isEmpty()) reversed.push(stack.pop());
            while (!reversed.isEmpty()) {
                var item = reversed.pop();
                stack.push(item);
                clone.push((T) item.clone());
            }
            return clone;
        }
    }
}
