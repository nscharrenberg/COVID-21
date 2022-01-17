package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

public abstract class HPAMacroActionsFactory extends MacroActionFactory2 {
    protected static HPAMacroActionsFactory getInstance() {
        if (!(instance instanceof HPAMacroActionsFactory)) instance = null;
        return (HPAMacroActionsFactory) (instance == null ? (instance = new HPAMacroActionsFactory() {
        }) : instance);
    }

    public static HPAMacroActionsFactory init(IState state, City city, Player player) {
        getInstance().initialise(state, city, player);
        return getInstance();
    }

    public MacroAction getNextMacroAction() {
        // if (LOG) logMacros(getActions()).forEach(this::append);
        MacroAction nextHPAMacro = getActions().get(0);
        int remaining = 4 - nextHPAMacro.size();
        if (remaining > 0) {
            nextHPAMacro =  this.fillMacroAction(nextHPAMacro);
        }
        GameStateFactory.getAnalyticsRepository().getCurrentGameAnalytics(state).getCurrentPlayerAnalytics(state).markMacroActionUsed(nextHPAMacro);
        return nextHPAMacro;

    }
}
