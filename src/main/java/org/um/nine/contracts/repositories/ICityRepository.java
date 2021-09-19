package org.um.nine.contracts.repositories;

import org.um.nine.domain.City;
import org.um.nine.exceptions.ResearchStationLimitException;

import java.util.HashMap;
import java.util.List;

public interface ICityRepository {
    HashMap<String, City> getCities();
    void addResearchStation(City city) throws ResearchStationLimitException;
    void reset();
}