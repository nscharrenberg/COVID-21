package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import org.checkerframework.checker.units.qual.A;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.domain.*;
import org.um.nine.exceptions.NoCubesLeftException;
import org.um.nine.utils.managers.RenderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiseaseRepository implements IDiseaseRepository {
    private List<InfectionRateMarker> infectionRate;
    private List<Marker> outbreakMarker;
    private HashMap<String, Cure> cures;

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
    public List<Marker> getOutbreakMarker() {
        return outbreakMarker;
    }

    @Override
    public HashMap<String, Cure> getCures() {
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

        this.outbreakMarker.add(new Marker(0, true));

        for (int i = 1; i <= 8; i++) {
            this.outbreakMarker.add(new Marker(i));
        }
    }

    private void initCures() {
        this.cures.put(ColorRGBA.Red.toString(), new Cure(ColorRGBA.Red));
        this.cures.put(ColorRGBA.Yellow.toString(), new Cure(ColorRGBA.Yellow));
        this.cures.put(ColorRGBA.Blue.toString(), new Cure(ColorRGBA.Blue));
        this.cures.put(ColorRGBA.Black.toString(), new Cure(ColorRGBA.Black));
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
    public void infect(ColorRGBA color, City city) throws NoCubesLeftException {
        Disease found = this.cubes.get(color).stream().filter(v -> v.getCity() == null).findFirst().orElse(null);

        if (found == null) {
            throw new NoCubesLeftException(color);
        }

        found.setCity(city);
        city.addCube(found);

        renderManager.renderDisease(found, city.getCubePosition(found));

        System.out.println("added cube");
    }

    @Override
    public void treat(Player pawn, City city, Disease disease) {
        city.getCubes().remove(disease);
        disease.setCity(null);

        if (cures.get(disease.getColor().toString()).isDiscovered()) {
            city.getCubes().forEach(cube -> {
                if (cube.getColor().equals(disease.getColor())) {
                    city.getCubes().remove(cube);
                    cube.setCity(null);
                }
            });
        }
    }
}
