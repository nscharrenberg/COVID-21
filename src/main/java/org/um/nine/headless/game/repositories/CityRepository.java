package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.contracts.repositories.ICityRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.ResearchStation;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.headless.game.exceptions.InvalidMoveException;
import org.um.nine.headless.game.exceptions.ResearchStationLimitException;
import org.um.nine.headless.game.utils.CityUtils;
import org.um.nine.v1.Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRepository implements ICityRepository {
    private HashMap<String, City> cities;
    private List<ResearchStation> researchStations;
    public CityRepository() {
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
        city.setResearchStation(new ResearchStation());
        this.getResearchStations().add(city.getResearchStation());
    }

    @Override
    public void addResearchStation(City city, Player player) throws Exception {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((researchStations.size() + 1) > Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        RoleAction action = RoleAction.BUILD_RESEARCH_STATION;
        if (player!= null){
            if (player.getRole().actions(action) && GameStateFactory.getInitialState().getBoardRepository().getSelectedRoleAction().equals(action) && !GameStateFactory.getInitialState().getBoardRepository().getUsedActions().contains(action)) {
                GameStateFactory.getInitialState().getBoardRepository().getUsedActions().add(action);
            } else {
                PlayerCard pc = player.getHand().stream().filter(c -> {
                    if (c instanceof CityCard cc) {
                        return cc.getCity().equals(player.getCity());
                    }

                    return false;
                }).findFirst().orElse(null);

                // If player doesn't have city card of his current city, it can't make this move.
                if (pc == null) {
                    throw new InvalidMoveException(city, player);
                }

                player.getHand().remove(pc);
            }
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
