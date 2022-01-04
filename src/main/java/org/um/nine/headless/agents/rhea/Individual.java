package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.Player;

import static org.um.nine.headless.game.Settings.BEST_HEURISTIC;

public final record Individual(Player player, MacroAction[] genome) {
    public double evaluateIndividual(IState state) {
        Player p = state.getPlayerRepository().getCurrentPlayer();
        state.getPlayerRepository().setCurrentPlayer(player());
        double f = BEST_HEURISTIC.evaluateState(state);
        state.getPlayerRepository().setCurrentPlayer(p);
        return f;
    }
}
