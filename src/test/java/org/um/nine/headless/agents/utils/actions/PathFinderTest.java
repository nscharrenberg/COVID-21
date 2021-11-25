package org.um.nine.headless.agents.utils.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.Difficulty;

class PathFinderTest {


    private IState state;
    private PathFinder x;

    @BeforeEach
    void setUp() {
        try {
            FactoryProvider.getPlayerRepository().createPlayer("Test1",false);
            FactoryProvider.getPlayerRepository().createPlayer("Test2",false);
            FactoryProvider.getBoardRepository().setDifficulty(Difficulty.EASY);
            FactoryProvider.getBoardRepository().start();
            FactoryProvider.getCityRepository().addResearchStation(
                    FactoryProvider.getCityRepository().
                            getCities().values().
                            stream().filter(city -> city.getName().equals("Tokyo")).findFirst().orElse(null)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = new State().getClonedState();
        x = new PathFinder(this.state);

    }

    @ParameterizedTest
    @ValueSource(booleans = {false,false,false,false})
    @DisplayName("FindWalkingDistanceFromCity")
    void testCostGraph(boolean testWalking, boolean testShuttleFlight,  boolean testDirectFlight, boolean testCharterFlight) {
        if (testWalking){
            x.evaluateCostGraphWalking();
            for (int i = 0; i<= 4; i++) {
                for (PathFinder.GCity gc : x.costGraph){
                    if (gc.nActionsWalking == i){
                        System.out.println(gc.city.getName() +" : "+ gc.nActionsWalking);
                    }
                }
            }
            for (PathFinder.GCity gc : x.costGraph){
                if (gc.nActionsWalking > 4 || gc.nActionsWalking < 0)
                    System.out.println("{"+gc.city.getName() +" : - }");
            }



            if (testShuttleFlight){
                x.evaluateCostGraphShuttleFlight();
                for (PathFinder.GCity gc : x.costGraph){
                    if (gc.nActionsShuttle != -1){
                        System.out.println(gc.city.getName() +" : "+ gc.nActionsShuttle);
                    }
                }
            }


        }
        if(testDirectFlight){
            x.evaluateCostGraphDirectFlight();
            System.out.println(state.getPlayerRepository().getCurrentPlayer().getHand().toString());

            for (PathFinder.GCity gc : x.costGraph) {
                System.out.println(gc.city.getName() + " : " + gc.nActionsDirectFlight.toString());
            }
        }
        if(testCharterFlight){
            x.evaluateCostGraphCharterFlight();
            System.out.println(state.getPlayerRepository().getCurrentPlayer().getHand().toString());
            for (PathFinder.GCity gc : x.costGraph) {
                System.out.println(gc.city.getName() + " : " + gc.nActionsCharterFlight.toString());
            }
        }

    }
}