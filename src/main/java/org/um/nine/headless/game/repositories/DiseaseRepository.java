package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.contracts.repositories.IDiseaseRepository;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.RoleEvent;
import org.um.nine.headless.game.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DiseaseRepository implements IDiseaseRepository {
    private List<InfectionRateMarker> infectionRates;
    private List<OutbreakMarker> outbreakMarkers;
    private HashMap<Color, Cure> cures;
    private HashMap<Color, List<Disease>> cubes;

    public DiseaseRepository() {
        this.infectionRates = new ArrayList<>();
        this.outbreakMarkers = new ArrayList<>();
        this.cubes = new HashMap<>();
        this.cures = new HashMap<>();
    }

    @Override
    public void nextOutbreak() throws GameOverException {
        OutbreakMarker marker = this.outbreakMarkers.stream().filter(Marker::isCurrent).findFirst().orElse(null);

        if (marker == null) {
            this.outbreakMarkers.get(0).setCurrent(true);
            return;
        }

        marker.setCurrent(false);
        OutbreakMarker nextMarker = this.outbreakMarkers.stream().filter(v -> v.getId() == marker.getId() + 1).findFirst().orElse(null);

        if (nextMarker == null) {
            throw new GameOverException();
        }

        nextMarker.setCurrent(true);
    }

    @Override
    public void nextInfectionMarker() {
        InfectionRateMarker marker = this.infectionRates.stream().filter(Marker::isCurrent).findFirst().orElse(null);

        if (marker == null) {
            this.infectionRates.get(0).setCurrent(true);
            return;
        }

        marker.setCurrent(false);
        InfectionRateMarker nextMarker = this.infectionRates.stream().filter(v -> v.getId() == marker.getId() + 1).findFirst().orElse(null);

        if (nextMarker == null) {
            return;
        }

        nextMarker.setCurrent(true);
    }

    @Override
    public void infect(Color color, City city) throws NoDiseaseOrOutbreakPossibleDueToEvent, NoCubesLeftException, GameOverException {
        if (cures.get(color).isDiscovered()
                && (cubes.get(color).stream().filter(c -> (c.getCity() != null)).findFirst().orElse(null) == null)) {
            return;
        }

        // Prevents both outbreaks and the placement of disease cubes in the city she is in
        for (Player player : city.getPawns() ) {
            if(player.getRole().events(RoleEvent.PREVENT_DISEASE_OR_OUTBREAK)) {
                throw new NoDiseaseOrOutbreakPossibleDueToEvent(city);
            }
        }

        // all cities connected to that city.
        for (City neighbor : city.getNeighbors()) {
            for (Player player : neighbor.getPawns() ) {
                if(player.getRole().events(RoleEvent.PREVENT_DISEASE_OR_OUTBREAK)) {
                    throw new NoDiseaseOrOutbreakPossibleDueToEvent(neighbor);
                }
            }
        }

        Disease found = this.cubes.get(color).stream().filter(v -> v.getCity() == null).findFirst().orElse(null);

        if (found == null) {
            throw new NoCubesLeftException(color);
        }

        found.setCity(city);
        if(!city.addCube(found)) {
            initOutbreak(city, found);
        }
    }

    @Override
    public void treat(Player pawn, City city, Color color) {
        if (cures.get(color).isDiscovered()
                || pawn.getRole().events(RoleEvent.REMOVE_ALL_CUBES_OF_A_COLOR)) {
            for (int i = city.getCubes().size() - 1; i >= 0; i--) {
                Disease cube = city.getCubes().get(i);

                if (cube.getColor().equals(color)) {
                    city.removeCube(cube);
                }
            }

            return;
        }

        city.removeCube(color);
    }

    @Override
    public void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException, GameWonException {
        if (pawn.getCity().getResearchStation() == null) {
            throw new UnableToDiscoverCureException(cure);
        }

        ArrayList<PlayerCard> pc = pawn.getHand().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().getColor().equals(cure.getColor());
            }

            return false;
        }).collect(Collectors.toCollection(ArrayList::new));

        int count = pc.size();

        if (pawn.getRole().events(RoleEvent.DISCOVER_CURE_FOUR_CARDS)) {
            if (count >= 4) {
                pawn.getHand().removeAll(pc);
                cure.setDiscovered(true);
                checkIfAllCured();
                return;
            }
        }

        if (count >= 5) {
            pawn.getHand().removeAll(pc);
            cure.setDiscovered(true);
            checkIfAllCured();

            return;
        }

        throw new UnableToDiscoverCureException(cure);
    }

    @Override
    public void reset() {
        this.infectionRates = new ArrayList<>();
        this.outbreakMarkers = new ArrayList<>();
        this.cures = new HashMap<>();
        this.cubes = new HashMap<>();
        this.cubes.put(Color.RED, new ArrayList<>());
        this.cubes.put(Color.YELLOW, new ArrayList<>());
        this.cubes.put(Color.BLUE, new ArrayList<>());
        this.cubes.put(Color.BLACK, new ArrayList<>());

        initCubes();
        initCures();
        initMarkers();
    }

    @Override
    public List<InfectionRateMarker> getInfectionRates() {
        return infectionRates;
    }

    @Override
    public void setInfectionRates(List<InfectionRateMarker> infectionRates) {
        this.infectionRates = infectionRates;
    }

    @Override
    public List<OutbreakMarker> getOutbreakMarkers() {
        return outbreakMarkers;
    }

    @Override
    public void setOutbreakMarkers(List<OutbreakMarker> outbreakMarkers) {
        this.outbreakMarkers = outbreakMarkers;
    }

    @Override
    public HashMap<Color, Cure> getCures() {
        return cures;
    }

    @Override
    public void setCures(HashMap<Color, Cure> cures) {
        this.cures = cures;
    }

    @Override
    public HashMap<Color, List<Disease>> getCubes() {
        return cubes;
    }

    @Override
    public void setCubes(HashMap<Color, List<Disease>> cubes) {
        this.cubes = cubes;
    }
}
