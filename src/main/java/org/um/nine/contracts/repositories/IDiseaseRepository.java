package org.um.nine.contracts.repositories;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.*;
import org.um.nine.exceptions.NoCubesLeftException;
import org.um.nine.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.exceptions.UnableToDiscoverCureException;

import java.util.HashMap;
import java.util.List;

public interface IDiseaseRepository {
    void reset();
    List<InfectionRateMarker> getInfectionRate();
    List<OutbreakMarker> getOutbreakMarker();
    HashMap<ColorRGBA, Cure> getCures();
    HashMap<ColorRGBA, List<Disease>> getCubes();
    void infect(ColorRGBA color, City city) throws NoCubesLeftException, OutbreakException, NoDiseaseOrOutbreakPossibleDueToEvent;
    void treat(Player pawn, City city, Disease disease);
    void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException;
}
