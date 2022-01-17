package org.um.nine.headless.agents.hybrid;

import org.junit.Test;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;

public class hybridTest {

    @Test
    public void macroActionTest(){
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        hybrid h = new hybrid();
        MacroAction m = h.run(state);
        System.out.println("Final move: " + m);
    }

}
