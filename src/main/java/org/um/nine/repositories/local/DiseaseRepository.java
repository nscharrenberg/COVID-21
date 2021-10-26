package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.*;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.RoleEvent;
import org.um.nine.exceptions.*;
import org.um.nine.screens.dialogs.GameEndState;
import org.um.nine.utils.managers.RenderManager;

import java.util.*;
import java.util.stream.Collectors;

public class DiseaseRepository implements IDiseaseRepository {
    private List<InfectionRateMarker> infectionRate;
    private List<OutbreakMarker> outbreakMarker;
    private HashMap<ColorRGBA, Cure> cures;

    private HashMap<ColorRGBA, List<Disease>> cubes;

    @Inject
    private RenderManager renderManager;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private GameEndState gameEndState;

    public DiseaseRepository() {
        reset();
    }

    @Override
    public List<InfectionRateMarker> getInfectionRate() {
        return infectionRate;
    }

    @Override
    public List<OutbreakMarker> getOutbreakMarker() {
        return outbreakMarker;
    }

    @Override
    public HashMap<ColorRGBA, Cure> getCures() {
        return cures;
    }

    @Override
    public HashMap<ColorRGBA, List<Disease>> getCubes() {
        return cubes;
    }

    private void initMarkers() {
        this.infectionRate.add(new InfectionRateMarker(0, 2, true));
        this.infectionRate.add(new InfectionRateMarker(1, 2));
        this.infectionRate.add(new InfectionRateMarker(2, 2));
        this.infectionRate.add(new InfectionRateMarker(3, 3));
        this.infectionRate.add(new InfectionRateMarker(4, 3));
        this.infectionRate.add(new InfectionRateMarker(5, 4));
        this.infectionRate.add(new InfectionRateMarker(6, 4));

        this.outbreakMarker.add(new OutbreakMarker(0, ColorRGBA.White, true));
        this.outbreakMarker.add(new OutbreakMarker(1, ColorRGBA.fromRGBA255(255, 235, 238, 1)));
        this.outbreakMarker.add(new OutbreakMarker(2, ColorRGBA.fromRGBA255(255, 205, 210, 1)));
        this.outbreakMarker.add(new OutbreakMarker(3, ColorRGBA.fromRGBA255(239, 154, 154, 1)));
        this.outbreakMarker.add(new OutbreakMarker(4, ColorRGBA.fromRGBA255(229, 115, 115, 1)));
        this.outbreakMarker.add(new OutbreakMarker(5, ColorRGBA.fromRGBA255(229, 57, 53, 1)));
        this.outbreakMarker.add(new OutbreakMarker(6, ColorRGBA.fromRGBA255(211, 47, 47, 1)));
        this.outbreakMarker.add(new OutbreakMarker(7, ColorRGBA.fromRGBA255(198, 40, 40, 1)));
        this.outbreakMarker.add(new OutbreakMarker(8, ColorRGBA.fromRGBA255(183, 28, 28, 1)));
    }

    private void initCures() {
        this.cures.put(ColorRGBA.Red, new Cure(ColorRGBA.Red));
        this.cures.put(ColorRGBA.Yellow, new Cure(ColorRGBA.Yellow));
        this.cures.put(ColorRGBA.Blue, new Cure(ColorRGBA.Blue));
        this.cures.put(ColorRGBA.Black, new Cure(ColorRGBA.Black));
    }

    private void initCubes() {
        for (int i = 0; i < 24; i++) {
            this.cubes.get(ColorRGBA.Red).add(new Disease(ColorRGBA.Red));
            this.cubes.get(ColorRGBA.Black).add(new Disease(ColorRGBA.Black));
            this.cubes.get(ColorRGBA.Blue).add(new Disease(ColorRGBA.Blue));
            this.cubes.get(ColorRGBA.Yellow).add(new Disease(ColorRGBA.Yellow));
        }
    }

    @Override
    public void nextOutbreak() throws GameOverException {
        OutbreakMarker marker = this.outbreakMarker.stream().filter(Marker::isCurrent).findFirst().orElse(null);

        if (marker == null) {
            this.outbreakMarker.get(0).setCurrent(true);
            return;
        }

        marker.setCurrent(false);
        OutbreakMarker newMarker = this.outbreakMarker.stream().filter(v -> v.getId() == marker.getId() + 1).findFirst().orElse(null);

        if (newMarker == null) {
            throw new GameOverException();
        }

        newMarker.setCurrent(true);
        renderManager.renderOutbreakMarker(marker);
        renderManager.renderOutbreakMarker(newMarker);
    }

    @Override
    public void nextInfectionMarker(){
        InfectionRateMarker marker = this.infectionRate.stream().filter(Marker::isCurrent).findFirst().orElse(null);

        if (marker == null) {
            this.infectionRate.get(0).setCurrent(true);
            return;
        }

        marker.setCurrent(false);
        InfectionRateMarker newMarker = this.infectionRate.stream().filter(v -> v.getId() == marker.getId() + 1).findFirst().orElse(null);

        if (newMarker == null) {
            return;
        }

        newMarker.setCurrent(true);
        renderManager.renderInfectionMarker(marker);
        renderManager.renderInfectionMarker(newMarker);
    }

    @Override
    public void reset() {
        this.infectionRate = new ArrayList<>();
        this.outbreakMarker = new ArrayList<>();
        this.cures = new HashMap<>();
        this.cubes = new HashMap<>();
        this.cubes.put(ColorRGBA.Red, new ArrayList<>());
        this.cubes.put(ColorRGBA.Yellow, new ArrayList<>());
        this.cubes.put(ColorRGBA.Blue, new ArrayList<>());
        this.cubes.put(ColorRGBA.Black, new ArrayList<>());

        initMarkers();
        initCures();
        initCubes();
    }

    @Override
    public void infect(ColorRGBA color, City city) throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        if (cures.get(color).isDiscovered() && (cubes.get(color).stream().filter(c -> (c.getCity() != null)).findFirst().orElse(null) == null)) {
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
            return;
        }

        renderManager.renderDisease(found, city.getCubePosition(found));
    }
    private void initOutbreak(City city, Disease disease) throws GameOverException {
        nextOutbreak();
        List<City> previousOutbreaks = new ArrayList<>();
        List<City> neighbors = city.getNeighbors();
        previousOutbreaks.add(city);

        for (City c: neighbors) {
            spreadOutbreak(c,disease,previousOutbreaks);
        }
    }

    private void spreadOutbreak(City city, Disease disease, List<City> previousOutbreaks) {
        if(!city.addCube(disease)){
            List<City> neighbors = city.getNeighbors();
            previousOutbreaks.add(city);

            for (City c: neighbors) {
                if(!previousOutbreaks.contains(c)) {
                    spreadOutbreak(c,disease,previousOutbreaks);
                }
            }

            return;
        }

        renderManager.renderDisease(disease, city.getCubePosition(disease));
    }

    @Override
    public void treat(Player pawn, City city, Disease disease) {
        String cubeName = disease.toString();

        city.getCubes().remove(disease);
        disease.setCity(null);

        Spatial cubeSpatial = gameRepository.getApp().getRootNode().getChild(cubeName);

        if (cubeSpatial != null) {
            cubeSpatial.removeFromParent();
        }

        if (cures.get(disease.getColor()).isDiscovered() || pawn.getRole().events(RoleEvent.REMOVE_ALL_CUBES_OF_A_COLOR)) {
            for (int i = city.getCubes().size()-1; i == 0; i--) {
                Disease cube = city.getCubes().get(i);

                if (cube.getColor().equals(disease.getColor())) {
                    String tempCubeName = cube.toString();
                    city.getCubes().remove(cube);
                    cube.setCity(null);

                    Spatial tempCubeSpatial = gameRepository.getApp().getRootNode().getChild(tempCubeName);

                    if (tempCubeSpatial != null) {
                        tempCubeSpatial.removeFromParent();
                    }
                }
            }
        }
    }

    @Override
    public void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException {
        if (pawn.getCity().getResearchStation() == null) {
            throw new UnableToDiscoverCureException(cure);
        }

        ArrayList<PlayerCard> pc = pawn.getHandCards().stream().filter(c -> {
            if (c instanceof CityCard) {
                CityCard cc = (CityCard) c;

                return cc.getCity().getColor().equals(cure.getColor());
            }

            return false;
        }).collect(Collectors.toCollection(ArrayList::new));

        long count = pc.size();

        if (pawn.getRole().events(RoleEvent.DISCOVER_CURE_FOUR_CARDS)) {
            if (count >= 4) {
                pawn.getHandCards().removeAll(pc);
                cure.setDiscovered(true);
                checkIfAllCured();
                return;
            }
        }

        if (count >= 5) {
            pawn.getHandCards().removeAll(pc);
            cure.setDiscovered(true);
            checkIfAllCured();

            return;
        }

        // else unable to discover a cure
        throw new UnableToDiscoverCureException(cure);
    }

    private void checkIfAllCured() {
        boolean eradicated = true;

        for (Map.Entry<ColorRGBA, Cure> entry : cures.entrySet()) {
            if (!entry.getValue().isDiscovered()) {
                eradicated = false;
                break;
            }
        }

        if (!eradicated) {
            return;
        }

        gameRepository.getApp().getStateManager().attach(gameEndState);
        gameEndState.setMessage("All cures discovered. You Win!");
        gameEndState.setEnabled(true);
    }

    @Override
    public void cleanup() {
        infectionRate = null;
        outbreakMarker = null;
        cures = null;
        cubes = null;
    }
}
