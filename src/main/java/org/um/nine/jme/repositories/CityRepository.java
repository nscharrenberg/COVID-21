package org.um.nine.jme.repositories;

import com.jme3.math.Vector3f;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.ResearchStation;
import org.um.nine.jme.utils.JmeFactory;

import java.util.HashMap;
import java.util.List;

public class CityRepository {

    private VisualRepository visualRepository = JmeFactory.getVisualRepository();

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
        visualRepository.renderResearchStation(city.getResearchStation(), new Vector3f(-20, 5, 0));
    }

    public void addResearchStation(City city, Player player) throws Exception {
        GameStateFactory.getInitialState().getCityRepository().addResearchStation(city, player);
        if (city.getResearchStation() != null) {
            visualRepository.renderResearchStation(city.getResearchStation(), new Vector3f(-20, 5, 0));
        }
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
        visualRepository = JmeFactory.getVisualRepository();
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
