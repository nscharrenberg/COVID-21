package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

import static org.um.nine.headless.agents.utils.Logger.addLog;
import static org.um.nine.headless.game.Settings.LOG;

public class RPAMacroActionsFactory extends MacroActionFactory {
    private static RPAMacroActionsFactory instance;

    @Override
    public MacroAction getNextMacroAction() {
        MacroAction nextRPAMacro = this.getActions().get(0);
        if (LOG) addLog("Best rpa macro : " + nextRPAMacro.toString());


        int remaining = 4 - nextRPAMacro.size();
        if (remaining > 0) {
            MacroAction filling = findSuitableMacro(nextRPAMacro);
            if (LOG) addLog("Filled rpa macro : " + filling);
            return filling;
        } else return nextRPAMacro;
    }

    protected static RPAMacroActionsFactory getInstance() {
        return instance == null ? (instance = new RPAMacroActionsFactory() {
        }) : instance;
    }

    public static RPAMacroActionsFactory init(IState state, City city, Player player) {
        MacroActionFactory.init(state, city, player);
        return instance = getInstance();
    }


    protected static List<MacroAction> buildRPAMacroActions() {
        actions = new ArrayList<>();
        var cure = buildCureDiseaseMacroActions();
        addList(cure, actions);
        var treat = buildTreatDiseaseMacroActions();
        addList(treat, actions);
        var build = buildResearchStationMacroActions();
        addList(build, actions);
        return actions;
    }

    @Override
    public List<MacroAction> getActions() {
        return actions == null ? (actions = buildRPAMacroActions()) : actions;
    }


}
