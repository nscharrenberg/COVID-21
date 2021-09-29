package org.um.nine.repositories.local;

import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.domain.Card;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.utils.cardmanaging.Shuffle;

import java.util.Stack;

public class CardRepository implements ICardRepository {
    private Stack<Card> playerDeck;
    private Stack<InfectionCard> infectionDeck;
    public CardRepository() {
    }

    @Override
    public Stack<Card> getPlayerDeck() {
        return playerDeck;
    }

    @Override
    public Stack<InfectionCard> getInfectionDeck() {
        return infectionDeck;
    }

    public void reset() {

    }

    public void buildDecks(ICityRepository cityRepository){
        this.playerDeck = Shuffle.buildPlayerDeck(4, cityRepository.getCities());
        this.infectionDeck = new Stack<>();
        Stack<Card> infection_deck =  Shuffle.buildEpidemicDeck(cityRepository.getCities());
        for (Card d : infection_deck){
            this.infectionDeck.push((InfectionCard) d);
        }
    }

}
