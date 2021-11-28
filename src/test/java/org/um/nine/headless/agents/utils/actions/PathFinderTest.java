package org.um.nine.headless.agents.utils.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.Difficulty;

class PathFinderTest {


    private IState state;
    private PathFinder.Descriptor pathFinder;

    @BeforeEach
    void setUp() {
        try {
            FactoryProvider.getPlayerRepository().createPlayer("Test1",false);
            FactoryProvider.getPlayerRepository().createPlayer("Test2",false);
            FactoryProvider.getBoardRepository().setDifficulty(Difficulty.EASY);
            FactoryProvider.getBoardRepository().start();
            FactoryProvider.getCityRepository().addResearchStation(
                    FactoryProvider.getCityRepository().getCities().get("Tokyo")
            );
            FactoryProvider.getCityRepository().addResearchStation(
                    FactoryProvider.getCityRepository().getCities().get("Cairo")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = new State().getClonedState(FactoryProvider.getInitialState());
        pathFinder = new PathFinder.Descriptor(this.state);

    }

    @Test
    @Disabled
    @DisplayName("GetShortestPath")
    void getShortestPath(){
        FactoryProvider.getCityRepository().getCities().
                values().forEach(c ->
                        System.out.println(c.getName() + " : " + pathFinder.shortestPath(c)));

    }


}