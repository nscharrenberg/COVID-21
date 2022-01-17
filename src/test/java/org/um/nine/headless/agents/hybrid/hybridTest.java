package org.um.nine.headless.agents.hybrid;

import org.junit.Test;
import org.um.nine.headless.agents.mcts.MacroMCTS;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.exceptions.GameOverException;

public class hybridTest {

    @Test
    public void macroActionTest(){
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        hybrid h = new hybrid();
        MacroAction m = h.run(state);
        System.out.println("Final move: " + m);
    }

    @Test
    public void macroTest(){
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        hybrid h = new hybrid();
        MacroActionsExecutor executor = new MacroActionsExecutor();
        while(!state.isGameLost() && !state.isGameWon()){
            try{
                MacroAction m = h.run(state);
                executor.executeIndexedMacro(state,m,true);
            } catch(GameOverException e){
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(state.isGameWon()) System.out.println("Won");
        else System.out.println("Lost");
    }
}
