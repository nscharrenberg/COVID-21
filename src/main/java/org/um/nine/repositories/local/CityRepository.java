package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.Player;
import org.um.nine.domain.ResearchStation;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.DiseaseAlreadyInCity;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.exceptions.ResearchStationLimitException;
import org.um.nine.utils.managers.RenderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRepository implements ICityRepository {
    private HashMap<String, City> cities;
    private List<ResearchStation> researchStations;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private RenderManager renderManager;

    @Inject
    private IDiseaseRepository diseaseRepository;

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

        if (city.getResearchStation() != null) {
            renderManager.renderResearchStation(city.getResearchStation(), new Vector3f(-20, 5, 0));
        }
    }

    @Override
    public void addDiseaseCube(City city, Disease cube) throws OutbreakException, DiseaseAlreadyInCity {
        if (cube.getCity() != null) {
            throw new DiseaseAlreadyInCity();
        }

        if ((city.getCubes().size() + 1) > Info.OUTBREAK_THRESHOLD) {
            throw new OutbreakException();
        }

        city.addCube(cube);

        renderManager.renderDisease(cube, city.getCubePosition(cube));
    }

    @Override
    public void addPawn(City city, Player player) {
        city.addPawn(player);

        renderManager.renderPlayer(player, city.getPawnPosition(player));
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

        renderCities();

        try {
            addResearchStation(atlanta);
        } catch (ResearchStationLimitException e) {
            e.printStackTrace();
        } catch (CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        }

        try {
            addDiseaseCube(atlanta, diseaseRepository.getYellowCubes().get(0));
            addDiseaseCube(atlanta, diseaseRepository.getRedCubes().get(0));
            addDiseaseCube(atlanta, diseaseRepository.getBlueCubes().get(1));
        } catch (OutbreakException e) {
            e.printStackTrace();
        } catch (DiseaseAlreadyInCity e) {
            e.printStackTrace();
        }

        playerRepository.reset();
    }

    @Override
    public void renderCities() {
        getCities().forEach((key, city) -> {
            city.getNeighbors().forEach(neighbor -> renderManager.renderEdge(city, neighbor));
            renderManager.renderCity(city);
        });
    }
}
