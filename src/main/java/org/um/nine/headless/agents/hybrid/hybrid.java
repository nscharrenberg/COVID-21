package org.um.nine.headless.agents.hybrid;

import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.agents.mcts.MacroMCTS;
import org.um.nine.headless.agents.mcts.Node;
import org.um.nine.headless.agents.rhea.core.IAgent;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.experiments.MacroNode;
import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.IState;

import java.util.stream.IntStream;

import static org.um.nine.headless.game.Settings.DEFAULT_MACRO_ACTIONS_EXECUTOR;
import static org.um.nine.headless.game.Settings.DEFAULT_PLAYERS;

public class hybrid {

    private int ITERATIONS = 10;

    public MacroAction run(IState state) {
        MacroMCTS mcts = new MacroMCTS(state, ITERATIONS);
        MacroAction MCTSAction = mcts.run(state);

        //RHEA
        Individual agent = new Individual(new MacroAction[5]);
        agent = agent.initGenome(state);
        MacroAction RHEAAction = HPAMacroActionsFactory.init(state, state.getPlayerRepository().getCurrentPlayer().getCity(), state.getPlayerRepository().getCurrentPlayer()).getNextMacroAction();

        if(MCTSAction.equals(RHEAAction)) return MCTSAction;
        double MCTSValue = evaluate(MCTSAction,state);
        double RHEAValue = evaluate(RHEAAction,state);

        if(MCTSValue > RHEAValue) return MCTSAction;
        else return RHEAAction;
    }

    private double evaluate(MacroAction a, IState state){
        IState cloned = state.clone();
        MacroActionsExecutor mae = new MacroActionsExecutor();
        try{
            mae.executeIndexedMacro(cloned,a,true);
        }catch (Exception ignored){
        }
        Node node = new Node(cloned);
        return node.getValue();
    }
}
