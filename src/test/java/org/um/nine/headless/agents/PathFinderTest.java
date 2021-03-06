package org.um.nine.headless.agents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.rhea.pathfinder.PathFinder;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;

class PathFinderTest {


    private PathFinder.Descriptor pathFinder;

    @BeforeEach
    void setUp() {
        try {
            GameStateFactory.createInitialState();
            GameStateFactory.getInitialState().reset();

            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Tokyo")
            );
            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Cairo")
            );
            GameStateFactory.getInitialState().start();


        } catch (Exception e) {
            e.printStackTrace();
        }
        IState state = GameStateFactory.getInitialState();
        pathFinder = new PathFinder.Descriptor(state);

    }

    @Test
    @DisplayName("GetShortestPath")
    void getShortestPath(){
        GameStateFactory.getInitialState().getCityRepository().getCities().
                values().forEach(c ->
                        System.out.println(c.getName() + " : " + pathFinder.shortestPath(c)));

    }


}