package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.Vector3f;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.Player;
import org.um.nine.domain.ResearchStation;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.DiseaseAlreadyInCity;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.exceptions.ResearchStationLimitException;
import org.um.nine.utils.cardmanaging.CityCardReader;
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
    private IBoardRepository boardRepository;

    @Inject
    private RenderManager renderManager;

    @Inject
    private IDiseaseRepository diseaseRepository;

    public CityRepository() {
        this.researchStations = new ArrayList<>();
        this.cities = new HashMap<>();
    }

    @Override
    public HashMap<String, City> getCities() {
        return cities;
    }

    @Override
    public void addResearchStation(City city, Player player) throws ResearchStationLimitException, CityAlreadyHasResearchStationException {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((researchStations.size() + 1) > Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        RoleAction action = RoleAction.BUILD_RESEARCH_STATION;
        if (player!= null){
            if (player.getRole().actions(action) && boardRepository.getSelectedAction().equals(action) && !boardRepository.getUsedActions().contains(action)) {
                //TODO: add without discarding city card
                // TODO: add to used actions
            } else {
                // TODO: Check if player has this city card, if not throw exception
                // TODO: else add research station and discard city card
            }
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
            throw new OutbreakException(city);
        }

        city.addCube(cube);

        renderManager.renderDisease(cube, city.getCubePosition(cube));
    }

    //@Override
    public void addPawn(City city, Player player) {
        city.addPawn(player);

        renderManager.renderPlayer(player, city.getPawnPosition(player));
    }

    @Override
    public void preload() {
        this.cities = new HashMap<>();

        CityCardReader ccr = new CityCardReader();
        try {
            this.cities = ccr.cityReader("Cards/CityCards.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        this.researchStations = new ArrayList<>();
        preload();

        renderCities();
        try {
            addResearchStation(cities.get("Atlanta"), null);
        } catch (ResearchStationLimitException | CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void renderCities() {
        getCities().forEach((key, city) -> {
            city.getNeighbors().forEach(neighbor -> renderManager.renderEdge(city, neighbor));
            renderManager.renderCity(city);
        });


    }

}
