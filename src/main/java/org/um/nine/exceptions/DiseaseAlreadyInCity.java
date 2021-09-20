package org.um.nine.exceptions;

public class DiseaseAlreadyInCity extends Exception {
    public DiseaseAlreadyInCity() {
        super("This disease is already present in a city.");
    }
}
