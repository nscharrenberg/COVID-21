package org.um.nine.exceptions;

import org.um.nine.domain.Cure;

public class UnableToDiscoverCureException extends Exception {
    private final Cure cure;

    public UnableToDiscoverCureException(Cure cure) {
        super("Could not discover the " + cure.getColor().toString() + " cure because the player has insufficient cards of this color.");
        this.cure = cure;
    }

    public Cure getCure() {
        return cure;
    }
}
