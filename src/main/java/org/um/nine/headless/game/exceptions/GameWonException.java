package org.um.nine.headless.game.exceptions;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;

public class GameWonException extends Exception {
    public GameWonException() {
        super("You win!");
    }
}
