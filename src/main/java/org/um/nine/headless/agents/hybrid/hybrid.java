package org.um.nine.headless.agents.hybrid;

import org.um.nine.headless.agents.mcts.MacroMCTS;
import org.um.nine.headless.agents.mcts.Node;
import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.macro.HPAMacroActionsFactory;
import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.macro.MacroActionsExecutor;
import org.um.nine.headless.agents.rhea.state.IState;

import static org.um.nine.headless.game.Settings.ROLLING_HORIZON;

public class hybrid {

    private final int ITERATIONS = 10;

    public MacroAction run(IState state) {
        MacroMCTS mcts = new MacroMCTS(state, ITERATIONS);
        MacroAction MCTSAction = mcts.run(state);

        //RHEA
        Individual agent = new Individual(new MacroAction[ROLLING_HORIZON]);
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
