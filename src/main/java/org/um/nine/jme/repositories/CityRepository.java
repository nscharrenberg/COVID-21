package org.um.nine.jme.repositories;

import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.ResearchStation;
import org.um.nine.headless.agents.state.GameStateFactory;

import java.util.HashMap;
import java.util.List;

public class CityRepository {

    public CityRepository() {
    }

    public HashMap<String, City> getCities() {
        return GameStateFactory.getInitialState().getCityRepository().getCities();
    }

    public void setCities(HashMap<String, City> cities) {
        GameStateFactory.getInitialState().getCityRepository().setCities(cities);
    }

    public void addResearchStation(City city) throws Exception {
        GameStateFactory.getInitialState().getCityRepository().addResearchStation(city);
    }

    /**
     * Import cities from JSON file
     */
    public void preload() {
        GameStateFactory.getInitialState().getCityRepository().preload();
    }

    /**
     * Reset the state back to its original state
     */
    public void reset() {
        GameStateFactory.getInitialState().getCityRepository().reset();
    }

    /**
     * Clear all data
     */
    public void cleanup() {
        GameStateFactory.getInitialState().getCityRepository().cleanup();
    }

    public List<ResearchStation> getResearchStations() {
        return GameStateFactory.getInitialState().getCityRepository().getResearchStations();
    }

    public void setResearchStations(List<ResearchStation> researchStations) {
        GameStateFactory.getInitialState().getCityRepository().setResearchStations(researchStations);
    }
}
