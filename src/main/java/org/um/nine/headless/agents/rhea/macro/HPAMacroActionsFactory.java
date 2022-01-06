package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

import static org.um.nine.headless.game.Settings.DEFAULT_MACRO_ACTIONS_EXECUTOR;

public abstract class HPAMacroActionsFactory extends MacroActionFactory {
    protected static HPAMacroActionsFactory instance;

    public static MacroAction getNextMacroAction(IState state) {
        Player currentPlayer = state.getPlayerRepository().getCurrentPlayer();
        init(state, currentPlayer.getCity(), currentPlayer);
        return getInstance().getActions().get(0);
    }

    protected static HPAMacroActionsFactory getInstance() {
        return instance == null ? (instance = new HPAMacroActionsFactory() {
        }) : instance;
    }

    public static HPAMacroActionsFactory init(IState state, City city, Player player) {
        MacroActionFactory.init(state, city, player);
        return instance = getInstance();
    }

    public static void initIndividualGene(IState state, Individual individual) {
        state = state.getClonedState();
        Player player = state.getPlayerRepository().getCurrentPlayer();
        City currentCity = player.getCity();


        for (int i = 0; i < individual.genome().length; i++) {
            individual.genome()[i] = init(state, currentCity, player).getActions().get(0);
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(state, individual.genome()[i], true);
            state.getPlayerRepository().setCurrentPlayer(player); //trick the game logic here to allow fault turn
            currentCity = player.getCity();
        }
    }

    protected static List<MacroAction> buildHPAMacroActions() {
        actions = new ArrayList<>();
        var cure = buildCureDiseaseMacroActions();
        addList(cure, actions);
        var treat3 = buildTreatDiseaseMacroActions(3);
        addList(treat3, actions);
        var build = buildResearchStationMacroActions();
        addList(build, actions);
        var treat2 = buildTreatDiseaseMacroActions(2);
        addList(treat2, actions);
        var treat1 = buildTreatDiseaseMacroActions(1);
        addList(treat1, actions);
        var walk = buildWalkAwayMacroActions(4);
        addList(walk, actions);
        return actions;
    }

    @Override
    public List<MacroAction> getActions() {
        return actions == null ? (actions = buildHPAMacroActions()) : actions;
    }
}
