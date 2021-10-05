package org.um.nine.repositories.local;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.cards.EpidemicCard;
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

    private Stack<PlayerCard> playerDeck;
    private Stack<InfectionCard> infectionDeck;
    private Stack<InfectionCard> infectionDiscardPile;
    public CardRepository() {
    }

    @Override
    public Stack<PlayerCard> getPlayerDeck() {
        return playerDeck;
    }

    @Override
    public Stack<InfectionCard> getInfectionDeck() {
        return infectionDeck;
    }

    public void reset() {
        this.playerDeck = new Stack<>();
        this.infectionDeck = new Stack<>();
        this.infectionDiscardPile = new Stack<>();
    }

    @Override
    public void drawPlayCard() {
        PlayerCard drawn = this.playerDeck.pop();

        if (drawn instanceof EpidemicCard) {
            // TODO: Epidemic Card logic
            return;
        }

        playerRepository.getCurrentPlayer().getHandCards().add(drawn);
    }

    @Override
    public void drawInfectionCard() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        InfectionCard infectionCard = this.infectionDeck.pop();

        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
        this.infectionDiscardPile.push(infectionCard);
    }

    @Override
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
