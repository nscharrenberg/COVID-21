package org.um.nine.repositories.local;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.lwjgl.Sys;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.ResearchStation;
import org.um.nine.domain.cards.CityCardReader;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.ResearchStationLimitException;

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
    public void addResearchStation(City city) throws ResearchStationLimitException, CityAlreadyHasResearchStationException {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((researchStations.size() + 1) > Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        this.researchStations.add(new ResearchStation(city));
    }

    public void reset() {
        this.researchStations = new ArrayList<>();
        this.cities = new HashMap<>();

        City[] cityArray = {};
        CityCardReader ccr = new CityCardReader();
        try{
            cityArray = ccr.cityReader();
        } catch(Exception e){
            System.err.println("Error during card reading");
            System.out.close();
        }

        for(City city : cityArray){
            this.cities.put(city.getName(),city);
        }

        //ToDo Add neighbours to the city
    }
}
