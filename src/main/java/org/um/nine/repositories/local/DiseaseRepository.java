package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.domain.*;
import org.um.nine.domain.roles.RoleEvent;
import org.um.nine.exceptions.NoCubesLeftException;
import org.um.nine.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.exceptions.OutbreakException;
import org.um.nine.exceptions.UnableToDiscoverCureException;
import org.um.nine.utils.managers.RenderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiseaseRepository implements IDiseaseRepository {
    private List<InfectionRateMarker> infectionRate;
    private List<OutbreakMarker> outbreakMarker;
    private HashMap<ColorRGBA, Cure> cures;

    // TODO: We could also use 1 array for all instead of 4.
    private HashMap<ColorRGBA, List<Disease>> cubes;

    @Inject
    private RenderManager renderManager;

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
    public void infect(ColorRGBA color, City city) throws NoCubesLeftException, OutbreakException, NoDiseaseOrOutbreakPossibleDueToEvent {
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
        if(!city.addCube(found))
            initOutbreak(city, found);

        renderManager.renderDisease(found, city.getCubePosition(found));
    }
    private static void initOutbreak(City city, Disease disease) {
        //TODO: increment outbreak marker
        List<City> previousOutbreaks = new ArrayList<>();
        List<City> neighbors = city.getNeighbors();
        previousOutbreaks.add(city);

        for (City c: neighbors) {
            spreadOutbreak(c,disease,previousOutbreaks);
        }
    }

    private static void spreadOutbreak(City city, Disease disease, List<City> previousOutbreaks) {
        if(!city.addCube(disease)){
            //TODO: increment outbreak marker
            List<City> neighbors = city.getNeighbors();
            previousOutbreaks.add(city);

            for (City c: neighbors) {
                if(!previousOutbreaks.contains(c)) //prevent outbreaks happening twice
                    spreadOutbreak(c,disease,previousOutbreaks);
            }

        }
    }

    @Override
    public void treat(Player pawn, City city, Disease disease) {
        if (pawn.getRole().events(RoleEvent.REMOVE_ALL_CUBES_OF_A_COLOR)) {
            city.getCubes().removeIf(d -> d.getColor().equals(disease.getColor()));
        }

        city.getCubes().remove(disease);
        disease.setCity(null);

        if (cures.get(disease.getColor()).isDiscovered()) {
            city.getCubes().forEach(cube -> {
                if (cube.getColor().equals(disease.getColor())) {
                    city.getCubes().remove(cube);
                    cube.setCity(null);
                }
            });
        }
    }

    @Override
    public void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException {
        if (pawn.getRole().events(RoleEvent.DISCOVER_CURE_FOUR_CARDS)) {
            // TODO: If pawn has 4 cards of same color, then discover cure
            return;
        }

        // TODO: if pawn has 5 cards of the same color, then discover cure

        // else unable to discover a cure
        throw new UnableToDiscoverCureException(cure);
    }
}
