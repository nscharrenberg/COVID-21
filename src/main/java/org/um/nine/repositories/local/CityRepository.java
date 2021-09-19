package org.um.nine.repositories.local;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.domain.City;

import java.util.ArrayList;
import java.util.List;

public class CityRepository implements ICityRepository {
    private List<City> cities;

    public CityRepository() {
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
}
