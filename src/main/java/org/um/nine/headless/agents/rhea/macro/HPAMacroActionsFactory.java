package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.core.Individual;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

import static org.um.nine.headless.agents.utils.Logger.addLog;
import static org.um.nine.headless.game.Settings.*;

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
        IState mutationState = state.getClonedState();
        Player player = mutationState.getPlayerRepository().getCurrentPlayer();
        City currentCity = player.getCity();

        if (LOG) {
            addLog("***\tInitializing player " + player.getId() + " genome...\t***");
        }


        for (int i = 0; i < individual.genome().length; i++) {
            if (LOG)
                addLog("Initializing player " + mutationState.getPlayerRepository().getCurrentPlayer().getId() + " genome, index " + i);
            MacroAction nextMacro = init(mutationState, currentCity, player).getNextMacroAction();
            individual.genome()[i] = nextMacro;
            DEFAULT_MACRO_ACTIONS_EXECUTOR.executeIndexedMacro(mutationState, nextMacro, true);
            mutationState.getPlayerRepository().setCurrentPlayer(player); //trick the game logic here to allow fault turn
            currentCity = player.getCity();
            ROUND_INDEX++;
        }
        ROUND_INDEX = 0;
    }
    public MacroAction getNextMacroAction() {
        MacroAction nextHPAMacro = getActions().get(0);
        if (LOG) addLog("Best hpa macro : " + nextHPAMacro.toString());


        int remaining = 4 - nextHPAMacro.size();
        if (remaining > 0) {
            MacroAction filling = findSuitableMacro(nextHPAMacro);
            if (LOG) addLog("Filled hpa macro : " + filling);
            return filling;
        } else return nextHPAMacro;
    }
    protected static List<MacroAction> buildHPAMacroActions() {
        if (LOG) addLog("Building allowed HPA macro actions...");
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
