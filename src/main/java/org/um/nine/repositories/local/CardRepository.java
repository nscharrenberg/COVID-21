package org.um.nine.repositories.local;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.Card;
import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.exceptions.GameOverException;
import org.um.nine.exceptions.NoCubesLeftException;
import org.um.nine.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.utils.cardmanaging.CityCardReader;
import org.um.nine.utils.cardmanaging.Shuffle;

import java.util.Collections;
import java.util.Stack;

public class CardRepository implements ICardRepository {
    @Inject
    private ICityRepository cityRepository;
    @Inject
    private IPlayerRepository playerRepository;
    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private IBoardRepository boardRepository;

    private Stack<Card> playerDeck;
    private Stack<InfectionCard> infectionDeck;
    private Stack<InfectionCard> infectionDiscardPile;
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

    public Stack<InfectionCard> getInfectionDiscardPile() {
        return infectionDiscardPile;
    }

    public void setInfectionDiscardPile(Stack<InfectionCard> newPile) {
        infectionDiscardPile = newPile;
    }

    public void reset() {

    }

    @Override
    public void drawPlayCard() {
        // TODO: Check if Epedemic Card
        // TODO: Epidemic Card loigc

        // TODO: else add to hand
    }

    public void buildDecks() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException, OutbreakException {
        this.playerDeck = Shuffle.buildPlayerDeck(boardRepository.getDifficulty(), cityRepository.getCities(), playerRepository.getPlayers());
        infectionDeck = CityCardReader.generateInfectionDeck(cityRepository.getCities().values().toArray(new City[0]));
        infectionDiscardPile = new Stack<>();
        Collections.shuffle(infectionDeck);

        //Set initial infection:
        //draw 3 cards 3 cubes, 3 cards 2 cubes, 3 cards 1 cube
        //and place cards on infection discard pile
        for(int i = 3;i>0;i--){
            for(int j = 0;j<3;j++){
                InfectionCard c = infectionDeck.pop();
                infectionDiscardPile.add(c);
                Disease d = new Disease(c.getCity().getColor());
                for(int k=i;k>0;k--) {
                    diseaseRepository.infect(d.getColor(),c.getCity());
                }
            }
        }
    }

}
