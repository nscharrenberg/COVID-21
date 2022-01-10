package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.roles.Medic;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.um.nine.headless.agents.rhea.state.StateEvaluation.Cd;
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
                List<ActionType.StandingAction> toTreat = actionCity.get(macro.standingActions().get(s).applyTo());
                int d = toTreat == null ? 0 : toTreat.size() - treated;
                treated += executeStandingAction(d, state, macro.standingActions().get(s));
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
        switch (action.action()) {
            case TREAT_DISEASE -> {
                Map<Color, List<Disease>> grouped = state.getPlayerRepository().getCurrentPlayer().getCity().getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
                if (state.getPlayerRepository().getCurrentPlayer().getRole() instanceof Medic) {
                    var x = grouped.values().stream().max(Comparator.comparingInt(List::size)).get();
                    obj = new Object[]{Objects.requireNonNull(x.get(0).getColor())};
                } else {
                    var c = grouped.entrySet().stream().filter(list -> list.getValue().size() >= n).findFirst().orElse(null);
                    obj = new Object[]{Objects.requireNonNull(c).getKey()};
                }
                treated = 1;
            }
            case SHARE_KNOWLEDGE -> {
                obj = new Object[]{
                        action.receiving(), state.getPlayerRepository().getCurrentPlayer().getHand().stream().filter(pc -> ((CityCard) pc).getCity().equals(action.applyTo()))
                };
            }
            case DISCOVER_CURE -> {
                Player p = state.getPlayerRepository().getCurrentPlayer();
                var cards = p.getHand().stream().collect(Collectors.groupingBy(c -> ((CityCard) c).getCity().getColor()));
                for (Color c : cards.keySet()) {
                    if (cards.get(c).size() >= Cd(p)) obj = new Object[]{
                            state.getDiseaseRepository().getCures().get(c)
                    };
                }
            }
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
