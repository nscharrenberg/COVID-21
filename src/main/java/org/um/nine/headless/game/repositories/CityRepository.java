package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.Info;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.ResearchStation;
import org.um.nine.headless.game.exceptions.ResearchStationLimitException;
import org.um.nine.headless.game.utils.CityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRepository {
    private HashMap<String, City> cities;
    private List<ResearchStation> researchStations;

    public CityRepository() {
        cleanup();
    }

    public HashMap<String, City> getCities() {
        return cities;
    }

    public void setCities(HashMap<String, City> cities) {
        this.cities = cities;
    }

    public void addResearchStation(City city) throws Exception {
        if (this.researchStations.size() >= 6) {
            throw new ResearchStationLimitException();
        }

        if (city.getResearchStation() != null) {
            throw new Exception("There is already a research station on this city");
        }

        city.setResearchStation(new ResearchStation());
    }

    public void preload() {
        cleanup();
        this.cities = CityUtils.reader("Cards/CityCards.json");
    }

    public void reset() {
        cleanup();

        preload();

        try {
            this.addResearchStation(cities.get(Info.START_CITY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        this.cities = new HashMap<>();
        this.researchStations = new ArrayList<>();
    }

    public List<ResearchStation> getResearchStations() {
        return researchStations;
    }

    public void setResearchStations(List<ResearchStation> researchStations) {
        this.researchStations = researchStations;
    }
}
