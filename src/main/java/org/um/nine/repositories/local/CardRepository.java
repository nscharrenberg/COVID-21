package org.um.nine.repositories.local;

import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.domain.cards.EpidemicCard;
import org.um.nine.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.List;

public class CardRepository implements ICardRepository {
    private List<PlayerCard> playerDeck;
    private List<EpidemicCard> epidemicDeck;

    public CardRepository() {
        reset();
    }

    @Override
    public List<PlayerCard> getPlayerDeck() {
        return playerDeck;
    }

    @Override
    public List<EpidemicCard> getEpidemicDeck() {
        return epidemicDeck;
    }

    public void reset() {
        this.playerDeck = new ArrayList<>();
        this.epidemicDeck = new ArrayList<>();
    }
}
