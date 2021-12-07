package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IDiseaseRepository {
    default void initMarkers() {
        getInfectionRates().add(new InfectionRateMarker(2, true));
        getInfectionRates().add(new InfectionRateMarker(2));
        getInfectionRates().add(new InfectionRateMarker(2));
        getInfectionRates().add(new InfectionRateMarker(3));
        getInfectionRates().add(new InfectionRateMarker(3));
        getInfectionRates().add(new InfectionRateMarker(4));
        getInfectionRates().add(new InfectionRateMarker(4));

        getOutbreakMarkers().add(new OutbreakMarker(Color.WHITE, true));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_1));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_2));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_3));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_4));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_5));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_6));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_7));
        getOutbreakMarkers().add(new OutbreakMarker(Color.RED_8));
    }

    default void initCures() {
        getCures().put(Color.RED, new Cure(Color.RED));
        getCures().put(Color.BLACK, new Cure(Color.BLACK));
        getCures().put(Color.BLUE, new Cure(Color.BLUE));
        getCures().put(Color.YELLOW, new Cure(Color.YELLOW));
    }

    default void initCubes() {
        for (int i = 0; i < 24; i++) {
            getCubes().get(Color.RED).add(new Disease(Color.RED));
            getCubes().get(Color.BLACK).add(new Disease(Color.BLACK));
            getCubes().get(Color.BLUE).add(new Disease(Color.BLUE));
            getCubes().get(Color.YELLOW).add(new Disease(Color.YELLOW));
        }
    }

    void nextOutbreak() throws GameOverException;

    void nextInfectionMarker();
    boolean isGameOver();

    void infect(Color color, City city) throws NoDiseaseOrOutbreakPossibleDueToEvent, NoCubesLeftException, GameOverException;

    default void initOutbreak(City city, Disease disease) throws GameOverException {
        nextOutbreak();
        System.out.println("OUTBREAK");
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

        for (Map.Entry<Color, Cure> entry : getCures().entrySet()) {
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