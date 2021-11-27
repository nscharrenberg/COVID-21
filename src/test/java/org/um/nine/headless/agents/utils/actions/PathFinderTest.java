package org.um.nine.headless.agents.utils.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.City;
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
        this.state = new State().getClonedState();
        pathFinder = new PathFinder.Descriptor(this.state);

    }


    @Disabled
    @ParameterizedTest
    @ValueSource(booleans = {false,false,false,false})
    @DisplayName("FindWalkingDistanceFromCity")
    void testCostGraph(boolean testWalking, boolean testShuttleFlight,  boolean testDirectFlight, boolean testCharterFlight) {
        if (testWalking){
            pathFinder.evaluateCostGraphWalking();
            for (int i = 0; i<= 4; i++) {
                for (PathFinder.GCity gc : pathFinder.getCostGraph()){
                    if (gc.walkingPath.walkingActionDepth == i){
                        System.out.println(gc.city.getName() +" : "+ gc.walkingPath.walkingActionDepth);
                    }
                }
            }
            for (PathFinder.GCity gc : pathFinder.getCostGraph()){
                if (gc.walkingPath.walkingActionDepth > 4 || gc.walkingPath.walkingActionDepth < 0)
                    System.out.println("{"+gc.city.getName() +" : - }");
            }



            if (testShuttleFlight){
                pathFinder.evaluateCostGraphShuttleFlight();
                for (PathFinder.GCity gc : pathFinder.getCostGraph()){
                    if (gc.shuttlePath.shuttleActionDepth != -1){
                        System.out.println(gc.city.getName() +" : "+ gc.shuttlePath.shuttleActionDepth);
                    }
                }
            }


        }
        if(testDirectFlight){
            pathFinder.evaluateCostGraphDirectFlight();
            System.out.println(state.getPlayerRepository().getCurrentPlayer().getHand().toString());

            for (PathFinder.GCity gc : pathFinder.getCostGraph()) {
                System.out.println(gc.city.getName() + " : " + gc.directFlightActionsDepthList.toString());
            }
        }
        if(testCharterFlight){
            pathFinder.evaluateCostGraphCharterFlight();
            System.out.println(state.getPlayerRepository().getCurrentPlayer().getHand().toString());
            for (PathFinder.GCity gc : pathFinder.getCostGraph()) {
                System.out.println(gc.city.getName() + " : " + gc.charterFlightActionsDepthList.toString());
            }
        }

    }

    @Test
    @Disabled
    @DisplayName("GetCityInfo")
    void GetCityInfo() {
        for (City city : FactoryProvider.getCityRepository().getCities().values()){
            System.out.println(pathFinder.getCityInfo(city));
        }
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