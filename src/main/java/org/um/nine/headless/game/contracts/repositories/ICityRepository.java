package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.ResearchStation;

import java.util.HashMap;
import java.util.List;

public interface ICityRepository extends Cloneable {

    ICityRepository clone();

    HashMap<String, City> getCities();

    void setCities(HashMap<String, City> cities);

    void addResearchStation(City city) throws Exception;

    void preload();

    void reset();

    void cleanup();

    List<ResearchStation> getResearchStations();

    void setResearchStations(List<ResearchStation> researchStations);

}
