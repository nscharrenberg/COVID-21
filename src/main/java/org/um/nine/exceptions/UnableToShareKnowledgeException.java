package org.um.nine.exceptions;

import org.um.nine.domain.City;
import org.um.nine.domain.Player;

public class UnableToShareKnowledgeException extends Exception {
    private final City city;
    private final Player player1;
    private final Player player2;

    public UnableToShareKnowledgeException(City city, Player player1, Player player2) {
        super("Neither " + player1.getName() + " nor " + player2.getName() + " has a city card for " + city.getName() + " to share");

        this.city = city;
        this.player1 = player1;
        this.player2 = player2;
    }
}
