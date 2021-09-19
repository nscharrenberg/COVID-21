package org.um.nine.repositories.local;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.ResearchStation;
import org.um.nine.exceptions.ResearchStationLimitException;

import java.util.ArrayList;
import java.util.List;

public class CityRepository implements ICityRepository {
    private List<City> cities;
    private List<ResearchStation> researchStations;

    public CityRepository() {
        this.researchStations = new ArrayList<>();
        this.cities = new ArrayList<>();

        // TODO: Utilize a JSON file to import all the cities and it's locations.

        City atlanta = new City("Atlanta", ColorRGBA.Blue, new Vector3f(-480, 182, 1));
        City chicago = new City("Chicago", ColorRGBA.Blue, new Vector3f(-500, 240, 1));
        atlanta.addNeighbour(chicago);

        this.cities.add(atlanta);
        this.cities.add(chicago);
    }

    @Override
    public List<City> getCities() {
        return cities;
    }

    @Override
    public void addResearchStation(City city) throws ResearchStationLimitException {
        if ((researchStations.size() + 1) > Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        this.researchStations.add(new ResearchStation(city));
    }
}
