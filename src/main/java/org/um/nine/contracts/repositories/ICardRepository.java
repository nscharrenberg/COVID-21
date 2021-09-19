package org.um.nine.contracts.repositories;

import org.um.nine.domain.cards.EpidemicCard;
import org.um.nine.domain.cards.PlayerCard;

import java.util.List;

public interface ICardRepository {
    List<PlayerCard> getPlayerDeck();
    List<EpidemicCard> getEpidemicDeck();
    void reset();
}
