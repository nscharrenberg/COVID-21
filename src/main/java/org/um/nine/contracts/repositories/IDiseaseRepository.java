package org.um.nine.contracts.repositories;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.*;
import org.um.nine.exceptions.NoCubesLeftException;

import java.util.HashMap;
import java.util.List;

public interface IDiseaseRepository {
    void reset();
    List<InfectionRateMarker> getInfectionRate();
    List<Marker> getOutbreakMarker();
    HashMap<String, Cure> getCures();
    HashMap<ColorRGBA, List<Disease>> getCubes();
    void infect(ColorRGBA color, City city) throws NoCubesLeftException;
    void treat(Player pawn, City city, Disease disease);
}
