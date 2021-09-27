package org.um.nine.contracts.repositories;

import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.Player;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.DiseaseAlreadyInCity;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.exceptions.ResearchStationLimitException;

import java.util.HashMap;

public interface ICityRepository {
    HashMap<String, City> getCities();
    void addResearchStation(City city, Player player) throws ResearchStationLimitException, CityAlreadyHasResearchStationException;
    void addDiseaseCube(City city, Disease cube) throws OutbreakException, DiseaseAlreadyInCity;
    void addPawn(City city, Player player);
    void reset();
    void renderCities();
}
