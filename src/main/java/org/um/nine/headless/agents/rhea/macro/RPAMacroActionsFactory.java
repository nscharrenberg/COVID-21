package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.Individual;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;

public class RPAMacroActionsFactory extends MacroActionFactory {
    private static RPAMacroActionsFactory instance;

    public static MacroAction getNextMacroAction(Individual individual, IState state) {
        init(state, individual.player().getCity(), individual.player());
        Collections.shuffle(getInstance().getActions(), RANDOM_PROVIDER);
        return getInstance().getActions().get(0);
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
