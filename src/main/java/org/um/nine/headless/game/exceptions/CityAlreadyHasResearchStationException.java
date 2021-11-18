package org.um.nine.headless.game.exceptions;

public class CityAlreadyHasResearchStationException extends Exception{
    public CityAlreadyHasResearchStationException() {
        super("The current city already has a research station.");
    }
}
