package org.um.nine.v1.contracts.repositories;

import com.jme3.math.ColorRGBA;
import org.um.nine.v1.domain.*;
import org.um.nine.v1.exceptions.*;

import java.util.HashMap;
import java.util.List;

public interface IDiseaseRepository {
    void nextOutbreak() throws GameOverException;

    void nextInfectionMarker();

    void reset();

    void cleanup();

    List<InfectionRateMarker> getInfectionRate();
    List<OutbreakMarker> getOutbreakMarker();
    HashMap<ColorRGBA, Cure> getCures();
    HashMap<ColorRGBA, List<Disease>> getCubes();
    void infect(ColorRGBA color, City city) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException;
    void treat(Player pawn, City city, Disease disease) throws NoCityCardToTreatDiseaseException;
    void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException;
}
