package org.um.nine.contracts.repositories;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.*;
import org.um.nine.exceptions.NoCubesLeftException;
import org.um.nine.exceptions.OutbreakException;

import java.util.HashMap;
import java.util.List;

public interface IDiseaseRepository {
    void reset();
    List<InfectionRateMarker> getInfectionRate();
    List<OutbreakMarker> getOutbreakMarker();
    HashMap<String, Cure> getCures();
    HashMap<ColorRGBA, List<Disease>> getCubes();
    void infect(ColorRGBA color, City city) throws NoCubesLeftException, OutbreakException;
    void treat(Player pawn, City city, Disease disease);
}
