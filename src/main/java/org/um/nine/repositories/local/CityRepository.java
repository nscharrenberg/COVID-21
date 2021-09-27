package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.*;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.EpidemicCard;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.domain.roles.RoleEvent;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.DiseaseAlreadyInCity;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.exceptions.ResearchStationLimitException;
import org.um.nine.utils.cardmanaging.CityCardReader;
import org.um.nine.utils.managers.RenderManager;

import java.util.ArrayList;
import java.util.Arrays;
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

        if (!player.getRole().actions(RoleAction.BUILD_RESEARCH_STATION)) {
            //TODO: discard city card
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

    @Override
    public void addPawn(City city, Player player) {
        city.addPawn(player);

        // f a disease has been cured, he automatically removes all cubes of that color from a city, simply by entering it or being there.
        if (player.getRole().events(RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE)) {
            city.getCubes().forEach(c -> {
                Cure found = diseaseRepository.getCures().get(c.getColor());

                if (found != null) {
                    if (found.isDiscovered()) {
                        city.getCubes().removeIf(cb -> cb.getColor().equals(found.getColor()));
                    }
                }
            });
        }

        renderManager.renderPlayer(player, city.getPawnPosition(player));
    }

    @Override
    public void reset() {
        this.researchStations = new ArrayList<>();
        this.cities = new HashMap<>();

        CityCardReader ccr = new CityCardReader();
        try {
            this.cities = ccr.cityReader("Cards/CityCards.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderCities();

        // TODO: remove all this stuff.
        playerRepository.reset();

        try {
            addResearchStation(cities.get("Atlanta"), playerRepository.getPlayers().get("example"));
        } catch (ResearchStationLimitException | CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        }

        try {
            addDiseaseCube(cities.get("Atlanta"), diseaseRepository.getCubes().get(ColorRGBA.Yellow).get(0));
            addDiseaseCube(cities.get("Atlanta"), diseaseRepository.getCubes().get(ColorRGBA.Red).get(0));
            addDiseaseCube(cities.get("Atlanta"), diseaseRepository.getCubes().get(ColorRGBA.Blue).get(1));
        } catch (OutbreakException e) {
            e.printStackTrace();
        } catch (DiseaseAlreadyInCity e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renderCities() {
        getCities().forEach((key, city) -> {
            city.getNeighbors().forEach(neighbor -> renderManager.renderEdge(city, neighbor));
            renderManager.renderCity(city);
        });

        //TODO: remove this test
        renderManager.renderPlayerCards(Arrays.asList(
                new CityCard(getCities().get("Atlanta")),
                new EpidemicCard("Plague"),
                new CityCard(getCities().get("Chicago")),
                new CityCard(getCities().get("Bangkok")),
                new EventCard("idk","might be important") {@Override public void event() {}},
                new CityCard(getCities().get("Mexico City")),
                new CityCard(getCities().get("Milan"))
        ));

    }
}
