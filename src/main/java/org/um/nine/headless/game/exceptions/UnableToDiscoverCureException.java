package org.um.nine.headless.game.exceptions;


import org.um.nine.headless.game.domain.Cure;

public class UnableToDiscoverCureException extends Exception {
    private final Cure cure;

    public UnableToDiscoverCureException(Cure cure) {
        super("Could not discover the " + cure.getColor().getName() + " cure because the player has insufficient cards of this color.");
        this.cure = cure;
    }

    public Cure getCure() {
        return cure;
    }
}
