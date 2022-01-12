package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.pathfinder.PathFinder2;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class HPAMacroActionsFactory extends MacroActionFactory {

    protected static HPAMacroActionsFactory getInstance() {
        if (!(instance instanceof HPAMacroActionsFactory)) instance = null;
        return (HPAMacroActionsFactory) (instance == null ? (instance = new HPAMacroActionsFactory() {
        }) : instance);
    }

    public static HPAMacroActionsFactory init(IState state, City city, Player player) {
        return getInstance().initialise(state, city, player);
    }

    public HPAMacroActionsFactory initialise(IState state, City city, Player player) {
        getInstance().state = state;
        getInstance().currentPlayer = player;
        getInstance().pathFinder = new PathFinder2(state, city, player);
        getInstance().actions = null;
        return getInstance();
    }

    public MacroAction getNextMacroAction() {
        MacroAction nextHPAMacro = getActions().get(0);


        int remaining = 4 - nextHPAMacro.size();
        if (remaining > 0) {
            return fillMacroAction(nextHPAMacro);
        } else return nextHPAMacro;
    }
    protected static List<MacroAction> buildHPAMacroActions() {
        getInstance().actions = new ArrayList<>();
        var cure = buildCureDiseaseMacroActions();
        addList(cure, getInstance().actions);
        var treat3 = buildTreatDiseaseMacroActions(3);
        addList(treat3, getInstance().actions);
        //var share = buildShareKnowledgeMacroActions(4);
        //addList(share,  getInstance().actions);
        var build = buildResearchStationMacroActions();
        addList(build, getInstance().actions);
        var treat2 = buildTreatDiseaseMacroActions(2);
        addList(treat2, getInstance().actions);
        var treat1 = buildTreatDiseaseMacroActions(1);
        addList(treat1, getInstance().actions);
        var walk = buildWalkAwayMacroActions(4);
        addList(walk, getInstance().actions);
        return getInstance().actions;
    }
    @Override
    public List<MacroAction> getActions() {
        return getInstance().actions == null ? (getInstance().actions = buildHPAMacroActions()) : getInstance().actions;
    }
}
