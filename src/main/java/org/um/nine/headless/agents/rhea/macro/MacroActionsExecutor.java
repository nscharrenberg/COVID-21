package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.agents.utils.Log;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.cards.CityCard;

import java.util.*;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.Settings.LOG;

public record MacroActionsExecutor(ExperimentalGame game) {

    public void runExperimentalGame() {
        IState initialState = game().getInitialState(),
                state = game.getCurrentState();

        while (game.onGoing()) {
            logPlayer(state);
            logDiseases(state);
            MacroAction nextMacro = MacroActionFactory.getNextMacroAction(state);
            executeMacroAction(state, nextMacro, true, true);
            game.getActionsHistory().add(nextMacro);
        }

        // resume from history test
        for (MacroAction m : game.getActionsHistory()) {
            logPlayer(initialState);
            logDiseases(initialState);
            executeIndexedMacro(initialState, m);
        }

        if (LOG) Log.log();
    }
    public void executeMacroAction(IState state, MacroAction nextMacro, boolean reverse, boolean draw) {
        int actionsLeft;
        if (reverse) {
            Collections.reverse(nextMacro.movingActions());
            Collections.reverse(nextMacro.standingActions());
        }

        actionsLeft = executeMovingActionsDefaultOrder(state, nextMacro);
        actionsLeft = executeStandingActionsDefaultOrder(actionsLeft, state, nextMacro);
        executeRestOfActions(actionsLeft, state, nextMacro, draw);
    }

    public void executeIndexedMacro(IState state, MacroAction macro) {
        char[] index = macro.index().toCharArray();
        int m, s = m = 0, treated = 0;
        Map<City, List<ActionType.StandingAction>> actionCity = macro.standingActions().stream().collect(Collectors.groupingBy(ActionType.StandingAction::applyTo));

        for (char c : index) {
            if (c == 'm') {
                executeMovingAction(state, macro.movingActions().get(m));
                m++;
                treated = 0;
            } else if (c == 's') {
                treated += executeStandingAction(actionCity.get(macro.standingActions().get(s).applyTo()).size() - treated, state, macro.standingActions().get(s));
                s++;
            }
        }
        try {
            state.getPlayerRepository().playerAction(null, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void logPlayer(IState state) {
        if (!LOG) return;
        Log.record("=============================================");
        Log.record("Player " +
                state.getPlayerRepository().
                        getCurrentPlayer() + " - " +
                state.getPlayerRepository().
                        getCurrentPlayer().
                        getRole().getName());

        Log.record("Cards in hand: " + state.getPlayerRepository().
                getCurrentPlayer().
                getHand().stream().
                map(c -> ((CityCard) c).getCity().getName() + " " + ((CityCard) c).getCity().getColor()).
                collect(Collectors.toList()));

    }

    public static void logDiseases(IState state) {
        if (!LOG) return;
        List<String> s = new ArrayList<>();
        for (City c : state.getCityRepository().getCities().values()) {
            Map<String, List<Disease>> grouped = c.getCubes().stream().collect(
                    Collectors.groupingBy(att -> att.getColor().getName())
            );
            if (!grouped.isEmpty())
                s.addAll(grouped.entrySet().
                        stream().
                        map(kv -> c.getName() + " (" +
                                kv.getValue().size() + " " +
                                kv.getKey() + ")").
                        collect(Collectors.toList()));
        }
        Log.record("Diseases : " + s);
    }

    public int executeMovingActionsDefaultOrder(IState state, MacroAction nextMacro) {
        int actions = 0;
        for (ActionType.MovingAction action : nextMacro.movingActions()) {
            executeMovingAction(state, action);
            actions++;
        }
        return actions;
    }

    public void executeMovingAction(IState state, ActionType.MovingAction action) {
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.toCity().getName()));
        try {
            if (LOG) Log.record("[" + action.action() + "] -> " + action.toCity());
            state.getPlayerRepository().playerAction(action.action(), state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int executeStandingActionsDefaultOrder(int actions, IState state, MacroAction nextMacro) {
        int treated = 0;
        for (ActionType.StandingAction action : nextMacro.standingActions()) {
            int n = nextMacro.standingActions().size() - treated;
            treated += executeStandingAction(n, state, action);
            actions++;
        }
        return actions;
    }

    public int executeStandingAction(int n, IState state, ActionType.StandingAction action) {
        int treated = 0;
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.applyTo().getName()));
        Object[] obj = null;
        if (action.action().equals(ActionType.TREAT_DISEASE)) {
            Map<Color, List<Disease>> grouped = state.getPlayerRepository().getCurrentPlayer().getCity().getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
            var c = grouped.entrySet().stream().filter(list -> list.getValue().size() >= n).findFirst().orElse(null);
            obj = new Object[]{Objects.requireNonNull(c).getKey()};
            treated = 1;
        }
        try {
            if (LOG) Log.record("[" + action.action() + "] -> " + action.applyTo());
            state.getPlayerRepository().playerAction(action.action(), state, obj == null ? new Object[]{} : obj);
        } catch (NullPointerException noDiseases) {
            //TODO: fix not allowed actions here
            try {
                state.getPlayerRepository().playerAction(ActionType.SKIP_ACTION, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return treated;
    }

    public void executeRestOfActions(int actions, IState state, MacroAction macro, boolean draw) {
        if (actions < 4) {
            MacroAction remaining = MacroActionFactory.findSuitableMacro(4 - actions);
            MacroAction.combine(macro, remaining);
            int nextActions = executeMovingActionsDefaultOrder(state, remaining);
            nextActions = executeStandingActionsDefaultOrder(nextActions, state, remaining);
            while (actions + nextActions != 4) {
                List<ActionType.StandingAction> s = new ArrayList<>();
                s.add(new ActionType.StandingAction(
                        ActionType.SKIP_ACTION,
                        state.getCityRepository().getCities().get(
                                state.getPlayerRepository().getCurrentPlayer().getCity().getName()
                        )
                ));
                MacroAction skip = MacroAction.macro(new ArrayList<>(), s);
                MacroAction.combine(macro, skip);

                try {
                    state.getPlayerRepository().playerAction(skip.standingActions().get(0).action(), state);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nextActions++;
            }
        }
        if (draw)
            try {
                state.getPlayerRepository().playerAction(null, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            state.getPlayerRepository().resetRound();
            state.getPlayerRepository().setCurrentRoundState(null);
        }
    }


}
