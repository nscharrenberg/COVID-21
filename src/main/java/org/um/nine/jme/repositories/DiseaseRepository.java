package org.um.nine.jme.repositories;

import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.exceptions.*;
import org.um.nine.jme.utils.JmeFactory;

import java.util.HashMap;
import java.util.List;

public class DiseaseRepository {

    private VisualRepository visualRepository = JmeFactory.getVisualRepository();

    public DiseaseRepository() {
    }

    /**
     * "Move" the outbreak marker to the next position.
     * 
     * @throws GameOverException - Thrown when its trying to exceed the last marker.
     */
    public void nextOutbreak() throws GameOverException {
        GameStateFactory.getInitialState().getDiseaseRepository().nextOutbreak();
        visualRepository.renderOutbreakMarker(GameStateFactory.getInitialState().getDiseaseRepository().getLastOutbreakMarker());
        visualRepository.renderOutbreakMarker(GameStateFactory.getInitialState().getDiseaseRepository().getCurrentOutbreakMarker());
    }

    /**
     * "Move" the infection marker to the next position.
     * Once it hits the end it'll stay at that position for the rest of the game.
     * (even though it shouldn't ever hit this)
     */
    public void nextInfectionMarker() {
        GameStateFactory.getInitialState().getDiseaseRepository().nextInfectionMarker();
        visualRepository.renderInfectionMarker(GameStateFactory.getInitialState().getDiseaseRepository().getLastInfectionRate());
        visualRepository.renderInfectionMarker(GameStateFactory.getInitialState().getDiseaseRepository().getCurrentInfectionRate());
    }

    public boolean isGameOver() {
        return GameStateFactory.getInitialState().getDiseaseRepository().isGameOver();
    }

    /**
     * Try to infect a city
     * Note 1: It wont infect when a player has the PREVENT_DISEASE_OR_OUTBREAK role
     * permission
     * Note 2: It will cause an outbreak when the 4th block is being added
     * 
     * @param color - the color of the disease to remove
     * @param city  - the city to remove a disease from
     * @throws NoDiseaseOrOutbreakPossibleDueToEvent - Thrown when a cube can't be
     *                                               place due to an event
     * @throws NoCubesLeftException                  - Thrown when no cubes of the
     *                                               correlating infection card are
     *                                               left.
     * @throws GameOverException                     - Thrown when the player lost
     *                                               the game
     */
    public void infect(Color color, City city)
            throws NoDiseaseOrOutbreakPossibleDueToEvent, NoCubesLeftException, GameOverException {
        GameStateFactory.getInitialState().getDiseaseRepository().infect(color, city);
    }

    /**
     * Treat a Disease
     * Note: If the pawn has a REMOVE_ALL_CUBES_OF_A_COLOR role permission it'll
     * remove all cubes with that color.
     * 
     * @param pawn  - The pawn that is trying to treat the disease
     * @param city  - The City that contains the disease
     * @param color - the color of the disease it should treat
     */
    public void treat(Player pawn, City city, Color color) {
        GameStateFactory.getInitialState().getDiseaseRepository().treat(pawn, city, color);
    }

    /**
     * Try to discover a cure and discard the required cards.
     * Note 1: When a pawn has the DISCOVER_CURE_FOUR_CARDS role permission it'll
     * only need 4 cards of the same color
     * Note 2: Only cures it when the pawn has sufficient cards of cure color (5 for
     * normal player, 4 if permission)
     * 
     * @param pawn - The pawn that is trying to discover the cure
     * @param cure - the cure that the pawn is trying to discover
     * @throws UnableToDiscoverCureException - Thrown when the pawn isn't able to
     *                                       find a cure
     * @throws GameWonException              - Thrown when all cures have been
     *                                       discovered
     */
    public void discoverCure(Player pawn, Cure cure) throws UnableToDiscoverCureException, GameWonException {
        GameStateFactory.getInitialState().getDiseaseRepository().discoverCure(pawn, cure);
    }

    /**
     * Resets the state back to its original data
     */
    public void reset() {
        visualRepository = JmeFactory.getVisualRepository();
        GameStateFactory.getInitialState().getDiseaseRepository().reset();
    }

    public List<InfectionRateMarker> getInfectionRates() {
        return GameStateFactory.getInitialState().getDiseaseRepository().getInfectionRates();
    }

    public void setInfectionRates(List<InfectionRateMarker> infectionRates) {
        GameStateFactory.getInitialState().getDiseaseRepository().setInfectionRates(infectionRates);
    }

    public List<OutbreakMarker> getOutbreakMarkers() {
        return GameStateFactory.getInitialState().getDiseaseRepository().getOutbreakMarkers();
    }

    public void setOutbreakMarkers(List<OutbreakMarker> outbreakMarkers) {
        GameStateFactory.getInitialState().getDiseaseRepository().setOutbreakMarkers(outbreakMarkers);
    }

    public HashMap<Color, Cure> getCures() {
        return GameStateFactory.getInitialState().getDiseaseRepository().getCures();
    }

    public void setCures(HashMap<Color, Cure> cures) {
        GameStateFactory.getInitialState().getDiseaseRepository().setCures(cures);
    }

    public HashMap<Color, List<Disease>> getCubes() {
        return GameStateFactory.getInitialState().getDiseaseRepository().getCubes();
    }

    public void setCubes(HashMap<Color, List<Disease>> cubes) {
        GameStateFactory.getInitialState().getDiseaseRepository().setCubes(cubes);
    }
}
