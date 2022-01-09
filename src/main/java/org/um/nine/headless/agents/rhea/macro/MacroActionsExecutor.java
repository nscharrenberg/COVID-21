package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Disease;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.Settings.LOG;

public record MacroActionsExecutor(ExperimentalGame game) {

    public void executeIndexedMacro(IState state, MacroAction macro, boolean draw) {
        char[] index = macro.index().toCharArray();
        int m, s = m = 0, treated = 0;
        Map<City, List<ActionType.StandingAction>> actionCity = macro.standingActions().stream().filter(sa -> sa.action().equals(ActionType.TREAT_DISEASE)).collect(Collectors.groupingBy(ActionType.StandingAction::applyTo));

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
        if (draw)
            try {
                state.getPlayerRepository().playerAction(null, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            state.getPlayerRepository().nextPlayer();
    }

    public void executeMovingAction(IState state, ActionType.MovingAction action) {
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.toCity().getName()));
        try {
            if (LOG) Logger.addLog("[" + action.action() + "] -> " + action.toCity());
            state.getPlayerRepository().playerAction(action.action(), state);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (LOG) Logger.addLog("[" + action.action() + "] -> " + action.applyTo());
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


}
