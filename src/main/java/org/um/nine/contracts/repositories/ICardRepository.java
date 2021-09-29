package org.um.nine.contracts.repositories;

import org.um.nine.domain.Card;
import org.um.nine.domain.cards.InfectionCard;

import java.util.Stack;

public interface ICardRepository {
    Stack<Card> getPlayerDeck();
    Stack<InfectionCard> getInfectionDeck();
    void reset();
}
