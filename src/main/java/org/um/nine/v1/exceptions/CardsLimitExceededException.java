package org.um.nine.v1.exceptions;

public class CardsLimitExceededException extends Exception {
    public CardsLimitExceededException () { super("Players can't have more than seven cards in their hand.");}
}
