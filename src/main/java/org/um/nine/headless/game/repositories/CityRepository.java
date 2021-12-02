package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.contracts.repositories.ICityRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.ResearchStation;
import org.um.nine.headless.game.exceptions.ResearchStationLimitException;
import org.um.nine.headless.game.utils.CityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRepository implements ICityRepository {
    private HashMap<String, City> cities;
    private List<ResearchStation> researchStations;
    public CityRepository() {
        reset();
    }

    @Override
    public HashMap<String, City> getCities() {
        return cities;
    }

    @Override
    public void setCities(HashMap<String, City> cities) {
        this.cities = cities;
    }

    @Override
    public void addResearchStation(City city) throws Exception {
        if (this.researchStations.size() >= 6) {
            throw new ResearchStationLimitException();
        }

        if (city.getResearchStation() != null) {
            throw new Exception("There is already a research station on this city");
        }

        city.setResearchStation(new ResearchStation());
        this.getResearchStations().add(city.getResearchStation());

    }

    /**
     * Import cities from JSON file
     */
    @Override
    public void preload() {
        this.cities = CityUtils.reader("Cards/CityCards.json");
    }

    /**
     * Reset the state back to its original state
     */
    @Override
    public void reset() {
        cleanup();
        preload();

        try {
            for (String city : Settings.RS)
                this.addResearchStation(this.getCities().get(city));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear all data
     */
    @Override
    public void cleanup() {
        this.cities = new HashMap<>();
        this.researchStations = new ArrayList<>();
    }

    @Override
    public List<ResearchStation> getResearchStations() {
        return researchStations;
    }

    @Override
    public void setResearchStations(List<ResearchStation> researchStations) {
        this.researchStations = researchStations;
    }
}
