package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.pathfinder.PathFinder2;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class RPAMacroActionsFactory extends MacroActionFactory {

    @Override
    public MacroAction getNextMacroAction() {
        MacroAction nextRPAMacro = this.getActions().get(0);

        int remaining = 4 - nextRPAMacro.size();
        if (remaining > 0) {
            return fillMacroAction(nextRPAMacro);
        } else return nextRPAMacro;
    }

    protected static RPAMacroActionsFactory getInstance() {
        if (!(instance instanceof RPAMacroActionsFactory)) instance = null;
        return (RPAMacroActionsFactory) (instance == null ? (instance = new RPAMacroActionsFactory() {
        }) : instance);
    }

    public static RPAMacroActionsFactory init(IState state, City city, Player player) {
        return getInstance().initialise(state, city, player);
    }

    public RPAMacroActionsFactory initialise(IState state, City city, Player player) {
        getInstance().state = state;
        getInstance().currentPlayer = player;
        getInstance().pathFinder = new PathFinder2(state, city, player);
        getInstance().actions = null;
        return getInstance();
    }

    protected static List<MacroAction> buildRPAMacroActions() {
        getInstance().actions = new ArrayList<>();
        var cure = buildCureDiseaseMacroActions();
        addList(cure, getInstance().actions);
        var treat = buildTreatDiseaseMacroActions();
        addList(treat, getInstance().actions);
        //var share = buildShareKnowledgeMacroActions(4);
        //addList(share, actions);
        var build = buildResearchStationMacroActions();
        addList(build, getInstance().actions);
        return getInstance().actions;
    }

    @Override
    public List<MacroAction> getActions() {
        return getInstance().actions == null ? (getInstance().actions = buildRPAMacroActions()) : getInstance().actions;
    }


}
