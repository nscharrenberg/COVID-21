package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.Individual;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class HPAMacroActionsFactory extends MacroActionFactory {
    protected static HPAMacroActionsFactory instance;

    public static MacroAction getNextMacroAction(Individual individual, IState state) {
        init(state, individual.player().getCity(), individual.player());
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

    public static void initIndividualGene(Individual individual, IState state) {
        MacroActionsExecutor simulator = new MacroActionsExecutor(
                new ExperimentalGame(state)
        );

        MacroActionsExecutor.logPlayer(state);
        MacroActionsExecutor.logDiseases(state);
        Player player = individual.player();
        City currentCity = player.getCity();

        for (int i = 0; i < individual.genome().length; i++) {
            individual.genome()[i] = init(state, currentCity, player).getActions().get(0);
            simulator.executeMacroAction(state, individual.genome()[i], true, false);
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
