package org.um.nine.headless.game.exceptions;

public class DiseaseAlreadyInCity extends Exception {
    public DiseaseAlreadyInCity() {
        super("This disease is already present in a city.");
    }
}
