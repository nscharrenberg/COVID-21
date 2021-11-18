package org.um.nine.v1.exceptions;

public class CityAlreadyHasResearchStationException extends Exception{
    public CityAlreadyHasResearchStationException() {
        super("The current city already has a research station.");
    }
}
