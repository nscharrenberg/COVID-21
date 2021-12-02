package org.um.nine.headless.game.domain.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.utils.PathFinder;
import org.um.nine.headless.game.GameStateFactory;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.state.IState;

class PathFinderTest {


    private IState state;
    private PathFinder.Descriptor pathFinder;

    @BeforeEach
    void setUp() {
        try {
            GameStateFactory.getInitialState().getPlayerRepository().createPlayer("Test1",false);
            GameStateFactory.getInitialState().getPlayerRepository().createPlayer("Test2",false);
            GameStateFactory.getInitialState().getBoardRepository().setDifficulty(Difficulty.EASY);
            GameStateFactory.getInitialState().getBoardRepository().start();
            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Tokyo")
            );
            GameStateFactory.getInitialState().getCityRepository().addResearchStation(
                    GameStateFactory.getInitialState().getCityRepository().getCities().get("Cairo")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = GameStateFactory.getInitialState().getClonedState();
        pathFinder = new PathFinder.Descriptor(this.state);

    }

    @Test
    @Disabled
    @DisplayName("GetShortestPath")
    void getShortestPath(){
        GameStateFactory.getInitialState().getCityRepository().getCities().
                values().forEach(c ->
                        System.out.println(c.getName() + " : " + pathFinder.shortestPath(c)));

    }


}