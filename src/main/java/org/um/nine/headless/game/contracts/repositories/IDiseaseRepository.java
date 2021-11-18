package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IDiseaseRepository {
    default void initMarkers() {
        this.infectionRates.add(new InfectionRateMarker(2, true));
        this.infectionRates.add(new InfectionRateMarker(2));
        this.infectionRates.add(new InfectionRateMarker(2));
        this.infectionRates.add(new InfectionRateMarker(3));
        this.infectionRates.add(new InfectionRateMarker(3));
        this.infectionRates.add(new InfectionRateMarker(4));
        this.infectionRates.add(new InfectionRateMarker(4));

        this.outbreakMarkers.add(new OutbreakMarker(Color.WHITE, true));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_1));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_2));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_3));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_4));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_5));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_6));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_7));
        this.outbreakMarkers.add(new OutbreakMarker(Color.RED_8));
    }

    default void initCures() {
        this.cures.put(Color.RED, new Cure(Color.RED));
        this.cures.put(Color.BLACK, new Cure(Color.BLACK));
        this.cures.put(Color.BLUE, new Cure(Color.BLUE));
        this.cures.put(Color.YELLOW, new Cure(Color.YELLOW));
    }

    default void initCubes() {
        for (int i = 0; i < 24; i++) {
            this.cubes.get(Color.RED).add(new Disease(Color.RED));
            this.cubes.get(Color.BLACK).add(new Disease(Color.BLACK));
            this.cubes.get(Color.BLUE).add(new Disease(Color.BLUE));
            this.cubes.get(Color.YELLOW).add(new Disease(Color.YELLOW));
        }
    }

    void nextOutbreak() throws GameOverException;

    void nextInfectionMarker();

    void infect(Color color, City city) throws NoDiseaseOrOutbreakPossibleDueToEvent, NoCubesLeftException, GameOverException;

    default void initOutbreak(City city, Disease disease) throws GameOverException {
        nextOutbreak();

        List<City> previousOutbreaks = new ArrayList<>();
        List<City> neighbors = city.getNeighbors();

        previousOutbreaks.add(city);

        for (City c : neighbors) {
            spreadOutbreak(c, disease, previousOutbreaks);
        }
    }

    default void spreadOutbreak(City city, Disease disease, List<City> previousOutbreaks) {
        if (city.addCube(disease)) {
            return;
        }

        List<City> neighbors = city.getNeighbors();
        previousOutbreaks.add(city);

        for (City c : neighbors) {
            if (!previousOutbreaks.contains(c)) {
                spreadOutbreak(c, disease, previousOutbreaks);
            }
        }
    }

    void treat(Player pawn, City city, Color color);

    void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException, GameWonException;

    default void checkIfAllCured() throws GameWonException {
        boolean eradicated = true;

        for (Map.Entry<Color, Cure> entry : cures.entrySet()) {
            if (!entry.getValue().isDiscovered()) {
                eradicated = false;
                break;
            }
        }

        if (!eradicated) {
            return;
        }

        throw new GameWonException();
    }

    void reset();

    List<InfectionRateMarker> getInfectionRates();

    void setInfectionRates(List<InfectionRateMarker> infectionRates);

    List<OutbreakMarker> getOutbreakMarkers();

    void setOutbreakMarkers(List<OutbreakMarker> outbreakMarkers);

    HashMap<Color, Cure> getCures();

    void setCures(HashMap<Color, Cure> cures);

    HashMap<Color, List<Disease>> getCubes();

    void setCubes(HashMap<Color, List<Disease>> cubes);
}
