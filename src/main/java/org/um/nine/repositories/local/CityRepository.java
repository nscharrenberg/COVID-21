package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.ResearchStation;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.ResearchStationLimitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRepository implements ICityRepository {
    private HashMap<String, City> cities;
    private List<ResearchStation> researchStations;

    @Inject
    private IGameRepository gameRepository;

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

    @Override
    public void reset() {
        this.researchStations = new ArrayList<>();
        this.cities = new HashMap<>();

        // TODO: Utilize a JSON file to import all the cities and it's locations.

        City atlanta = new City("Atlanta", ColorRGBA.Blue, new Vector3f(-480, 182, 1));
        City chicago = new City("Chicago", ColorRGBA.Blue, new Vector3f(-500, 240, 1));
        atlanta.addNeighbour(chicago);

        this.cities.put(atlanta.getName(), atlanta);
        this.cities.put(chicago.getName(), chicago);
    }

    @Override
    public void renderResearchStations() {

    }

    @Override
    public void renderCities() {
        getCities().forEach((key, city) -> {
            city.getNeighbors().forEach(neighbor -> renderEdge(city, neighbor));

            renderCity(city);
        });

    }

    private void renderCity(City city) {
        Cylinder plateShape = new Cylinder(5, 10, 12.5f, 2, true);
        Geometry plate = new Geometry(city.getName(), plateShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", city.getColor());
        mat.setColor("GlowColor", city.getColor());
        gameRepository.refreshFpp();
        plate.setMaterial(mat);
        plate.setLocalTranslation(city.getLocation());
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    private void renderEdge(City city1, City city2) {
        Line lineShape = new Line(city1.getLocation(), city2.getLocation());
        Geometry plate = new Geometry(city1.getName() + "->" + city2.getName(), lineShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.White);
        mat.getAdditionalRenderState().setLineWidth(1);
        mat.getAdditionalRenderState().setWireframe(true);
        plate.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(plate);
    }
}
