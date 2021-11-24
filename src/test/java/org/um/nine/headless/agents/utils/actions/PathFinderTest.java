package org.um.nine.headless.agents.utils.actions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.domain.Difficulty;

class PathFinderTest {

    @Test
    @DisplayName("EvaluateCostGraph")
    void EvaluateCostGraph() {
        initializeGame();

        IState state = new State().getClonedState();

        PathFinder x = new PathFinder(state);
        x.evaluateCostGraph();

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking == 0) System.out.println(gc.city.getName() +" : "+ gc.nActionsWalking);
        }

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking == 1) System.out.println("CITY: "+gc.city.getName() +" : "+ gc.nActionsWalking);
        }

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking == 2) System.out.println("CITY: "+gc.city.getName() +" : "+ gc.nActionsWalking);
        }

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking == 3) System.out.println("CITY: "+gc.city.getName() +" : "+ gc.nActionsWalking);
        }

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking == 4) System.out.println("CITY: "+gc.city.getName() +" : "+ gc.nActionsWalking);
        }

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking > 4) System.out.println("CITY: "+gc.city.getName() +" : "+ gc.nActionsWalking);
        }

        for (PathFinder.GCity gc : x.costGraph){
            if (gc.nActionsWalking == -1) System.out.println(gc.city.getName() +" : "+ gc.nActionsWalking);
        }
    }

    void initializeGame(){
        try {
            FactoryProvider.getPlayerRepository().createPlayer("Test1",false);
            FactoryProvider.getPlayerRepository().createPlayer("Test2",false);
            FactoryProvider.getBoardRepository().setDifficulty(Difficulty.EASY);
            FactoryProvider.getBoardRepository().start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("FindWalkingDistanceFromCity")
    void FindWalkingDistanceFromCity() {
    }
}