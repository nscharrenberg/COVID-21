package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.domain.Cure;
import org.um.nine.domain.Disease;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.OutbreakMarker;
import org.um.nine.utils.managers.RenderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiseaseRepository implements IDiseaseRepository {
    private List<InfectionRateMarker> infectionRate;
    private List<OutbreakMarker> outbreakMarker;
    private HashMap<String, Cure> cures;

    // TODO: We could also use 1 array for all instead of 4.
    private List<Disease> blackCubes;
    private List<Disease> yellowCubes;
    private List<Disease> blueCubes;
    private List<Disease> redCubes;

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
    public HashMap<String, Cure> getCures() {
        return cures;
    }

    @Override
    public List<Disease> getBlackCubes() {
        return blackCubes;
    }

    @Override
    public List<Disease> getYellowCubes() {
        return yellowCubes;
    }

    @Override
    public List<Disease> getBlueCubes() {
        return blueCubes;
    }

    @Override
    public List<Disease> getRedCubes() {
        return redCubes;
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
        this.cures.put(ColorRGBA.Red.toString(), new Cure(ColorRGBA.Red));
        this.cures.put(ColorRGBA.Yellow.toString(), new Cure(ColorRGBA.Yellow));
        this.cures.put(ColorRGBA.Blue.toString(), new Cure(ColorRGBA.Blue));
        this.cures.put(ColorRGBA.Black.toString(), new Cure(ColorRGBA.Black));
    }

    private void initCubes() {
        for (int i = 0; i < 24; i++) {
            this.redCubes.add(new Disease(ColorRGBA.Red));
            this.blackCubes.add(new Disease(ColorRGBA.Black));
            this.yellowCubes.add(new Disease(ColorRGBA.Yellow));
            this.blueCubes.add(new Disease(ColorRGBA.Blue));
        }
    }

    @Override
    public void reset() {
        this.infectionRate = new ArrayList<>();
        this.outbreakMarker = new ArrayList<>();
        this.cures = new HashMap<>();
        this.redCubes = new ArrayList<>();
        this.yellowCubes = new ArrayList<>();
        this.blueCubes = new ArrayList<>();
        this.blackCubes = new ArrayList<>();

        initMarkers();
        initCures();
        initCubes();
    }

    private void renderCures() {

    }
}
