package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.Vector3f;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.domain.ResearchStation;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.InvalidMoveException;
import org.um.nine.exceptions.NoCityCardToBuildResearchStation;
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
    public void addResearchStation(City city, Player player) throws ResearchStationLimitException, CityAlreadyHasResearchStationException, InvalidMoveException, NoCityCardToBuildResearchStation {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((researchStations.size() + 1) > Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        RoleAction action = RoleAction.BUILD_RESEARCH_STATION;
        if (player!= null){
            if (player.getRole().actions(action) && boardRepository.getSelectedRoleAction() != null && boardRepository.getSelectedRoleAction().equals(action) && !boardRepository.getUsedActions().contains(action)) {
                boardRepository.getUsedActions().add(action);
            } else {
                PlayerCard pc = player.getHandCards().stream().filter(c -> {
                    if (c instanceof CityCard cc) {
                        return cc.getCity().equals(player.getCity());
                    }

                    return false;
                }).findFirst().orElse(null);

                // If player doesn't have city card of his current city, it can't make this move.
                if (pc == null) {
                    throw new NoCityCardToBuildResearchStation(player);
                }

                player.getHandCards().remove(pc);
            }
        }


        this.researchStations.add(new ResearchStation(city));

        if (city.getResearchStation() != null) {
            renderManager.renderResearchStation(city.getResearchStation(), new Vector3f(-20, 5, 0));
        }
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
        } catch (ResearchStationLimitException | CityAlreadyHasResearchStationException | InvalidMoveException | NoCityCardToBuildResearchStation e) {
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

    @Override
    public void cleanup() {
        cities = null;
        researchStations = null;
    }
}
