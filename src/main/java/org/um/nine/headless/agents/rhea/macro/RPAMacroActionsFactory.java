package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.um.nine.headless.game.Settings.RANDOM_PROVIDER;

public abstract class RPAMacroActionsFactory extends MacroActionFactory2 {
    @Override
    public MacroAction getNextMacroAction() {
        List<MacroAction> allActions;
        if (getActions().isEmpty()) throw new IllegalStateException();
        allActions = new ArrayList<>(getActions());
        Collections.shuffle(allActions, RANDOM_PROVIDER);
        return fillMacroAction(allActions.get(0));
    }

    protected static RPAMacroActionsFactory getInstance() {
        if (!(instance instanceof RPAMacroActionsFactory)) instance = null;
        return (RPAMacroActionsFactory) (instance == null ? (instance = new RPAMacroActionsFactory() {
        }) : instance);
    }

    public static RPAMacroActionsFactory init(IState state, City city, Player player) {
        getInstance().initialise(state, city, player);
        return getInstance();
    }

}
