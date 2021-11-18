package org.um.nine.v1.contracts.repositories;

import org.um.nine.v1.domain.City;
import org.um.nine.v1.domain.Player;
import org.um.nine.v1.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.v1.exceptions.InvalidMoveException;
import org.um.nine.v1.exceptions.ResearchStationLimitException;

import java.util.HashMap;

public interface ICityRepository {
    HashMap<String, City> getCities();
    void addResearchStation(City city, Player player) throws ResearchStationLimitException, CityAlreadyHasResearchStationException, InvalidMoveException;

    void preload();

    void reset();
    void renderCities();

    void cleanup();
}
