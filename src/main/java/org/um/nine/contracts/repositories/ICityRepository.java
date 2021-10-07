package org.um.nine.contracts.repositories;

import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.InvalidMoveException;
import org.um.nine.exceptions.ResearchStationLimitException;

import java.util.HashMap;

public interface ICityRepository {
    HashMap<String, City> getCities();
    void addResearchStation(City city, Player player) throws ResearchStationLimitException, CityAlreadyHasResearchStationException, InvalidMoveException;

    void preload();

    void reset();
    void renderCities();
}
