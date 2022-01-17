package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.contracts.repositories.IDiseaseRepository;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.RoleEvent;
import org.um.nine.headless.game.exceptions.*;
import org.um.nine.jme.utils.JmeFactory;
import org.um.nine.v1.Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiseaseRepository implements IDiseaseRepository {

    private List<InfectionRateMarker> infectionRates;
    private List<OutbreakMarker> outbreakMarkers;
    private OutbreakMarker currentOutbreakMarker;
    private OutbreakMarker lastOutbreakMarker;
    private HashMap<Color, Cure> cures;
    private HashMap<Color, List<Disease>> cubes;
    private boolean gameOver;
    private InfectionRateMarker currentInfectionRate;
    private InfectionRateMarker lastInfectionRate;
    private Disease diseaseCube;
    public DiseaseRepository() {
    }

    @Override
    public DiseaseRepository clone() {
        try {
            DiseaseRepository clone = (DiseaseRepository) super.clone();
            clone.setInfectionRates(this.getInfectionRates().stream().map(InfectionRateMarker::clone).collect(Collectors.toList()));
            clone.setOutbreakMarkers(this.getOutbreakMarkers().stream().map(OutbreakMarker::clone).collect(Collectors.toList()));
            clone.setCubes(new HashMap<>());
            this.getCubes().forEach((color, diseases) -> clone.getCubes().put(color, diseases.stream().map(Disease::clone).collect(Collectors.toList())));
            clone.setCures(new HashMap<>());
            this.getCures().forEach((color, cure) -> clone.getCures().put(color, cure.clone()));
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * "Move" the outbreak marker to the next position.
     *
     * @throws GameOverException - Thrown when its trying to exceed the last marker.
     */
    @Override
    public void nextOutbreak() throws GameOverException {
        OutbreakMarker marker = this.outbreakMarkers.stream().filter(Marker::isCurrent).findFirst().orElse(null);
        lastOutbreakMarker = marker;
        if (marker == null) {
            this.outbreakMarkers.get(0).setCurrent(true);
            return;
        }

        marker.setCurrent(false);
        OutbreakMarker nextMarker = this.outbreakMarkers.stream().filter(v -> v.getId() == marker.getId() + 1).findFirst().orElse(null);

        if (nextMarker == null) {
            this.gameOver = true;
            throw new GameOverException();
        }

        nextMarker.setCurrent(true);
        currentOutbreakMarker = nextMarker;
    }

    /**
     * "Move" the infection marker to the next position.
     * Once it hits the end it'll stay at that position for the rest of the game. (even though it shouldn't ever hit this)
     */
    @Override
    public void nextInfectionMarker() {
        InfectionRateMarker marker = this.infectionRates.stream().filter(Marker::isCurrent).findFirst().orElse(null);
        lastInfectionRate = marker;
        if (marker == null) {
            this.infectionRates.get(0).setCurrent(true);
            return;
        }

        marker.setCurrent(false);
        InfectionRateMarker nextMarker = this.infectionRates.stream().filter(v -> v.getId() == marker.getId() + 1).findFirst().orElse(null);

        // Just incase something went wrong and we are exceeding our markers
        if (nextMarker == null) {
            return;
        }

        currentInfectionRate = nextMarker;
        nextMarker.setCurrent(true);
    }

    @Override
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * Try to infect a city
     * Note 1: It wont infect when a player has the PREVENT_DISEASE_OR_OUTBREAK role permission
     * Note 2: It will cause an outbreak when the 4th block is being added
     * @param color - the color of the disease to remove
     * @param city - the city to remove a disease from
     * @throws NoDiseaseOrOutbreakPossibleDueToEvent - Thrown when a cube can't be place due to an event
     * @throws NoCubesLeftException - Thrown when no cubes of the correlating infection card are left.
     * @throws GameOverException - Thrown when the player lost the game
     */
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
        diseaseCube = found;
        if (found == null) {
            this.gameOver = true;
            throw new NoCubesLeftException(color);
        }

        found.setCity(city);
        if(!city.addCube(found)) {
            initOutbreak(city, found);
        }

        if(Info.visualize){
            JmeFactory.getVisualRepository().renderDisease(found, city.getCubePosition(found));
        }
    }

    /**
     * Treat a Disease
     * Note: If the pawn has a REMOVE_ALL_CUBES_OF_A_COLOR role permission it'll remove all cubes with that color.
     * @param pawn - The pawn that is trying to treat the disease
     * @param city - The City that contains the disease
     * @param color - the color of the disease it should treat
     */
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

    /**
     * Try to discover a cure and discard the required cards.
     * Note 1: When a pawn has the DISCOVER_CURE_FOUR_CARDS role permission it'll only need 4 cards of the same color
     * Note 2: Only cures it when the pawn has sufficient cards of cure color (5 for normal player, 4 if permission)
     * @param pawn - The pawn that is trying to discover the cure
     * @param cure - the cure that the pawn is trying to discover
     * @throws UnableToDiscoverCureException - Thrown when the pawn isn't able to find a cure
     * @throws GameWonException - Thrown when all cures have been discovered
     */
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

    /**
     * Resets the state back to its original data
     */
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
        this.gameOver = false;

        this.initCubes();
        this.initCures();
        this.initMarkers();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiseaseRepository that = (DiseaseRepository) o;
        return gameOver == that.gameOver &&
                Objects.equals(infectionRates, that.infectionRates) &&
                Objects.equals(outbreakMarkers, that.outbreakMarkers) &&
                Objects.equals(cures, that.cures) &&
                Objects.equals(cubes, that.cubes);
    }


    @Override
    public OutbreakMarker getCurrentOutbreakMarker() {
        return currentOutbreakMarker;
    }

    @Override
    public OutbreakMarker getLastOutbreakMarker() {
        return lastOutbreakMarker;
    }

    @Override
    public InfectionRateMarker getCurrentInfectionRate() {
        return currentInfectionRate;
    }

    @Override
    public InfectionRateMarker getLastInfectionRate() {
        return lastInfectionRate;
    }

    @Override
    public Disease getDiseaseCube() {
        return diseaseCube;
    }
}
