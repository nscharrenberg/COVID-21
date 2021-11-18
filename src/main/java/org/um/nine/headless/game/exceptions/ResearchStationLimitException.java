package org.um.nine.headless.game.exceptions;

public class ResearchStationLimitException extends Exception {
    public ResearchStationLimitException() {
        super("There are already 6 research stations on the board. You must choose an existing research station to be moved to this city.");
    }
}
