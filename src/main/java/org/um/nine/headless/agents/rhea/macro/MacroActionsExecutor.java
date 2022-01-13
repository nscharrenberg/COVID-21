package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.roles.Medic;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.Cd;

public record MacroActionsExecutor() {

    public void executeIndexedMacro(IState state, MacroAction macro, boolean draw) throws Exception {
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
            state.getPlayerRepository().playerAction(null, state);
        else
            state.getPlayerRepository().nextPlayer();
    }

    public void executeMovingAction(IState state, ActionType.MovingAction action) throws Exception {
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.toCity().getName()));
        state.getPlayerRepository().playerAction(action.action(), state);
    }

    public int executeStandingAction(int n, IState state, ActionType.StandingAction action) throws Exception {
        int treated = 0;
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.applyTo().getName()));
        Object[] obj = null;
        ActionType executedAction = action.action();
        switch (action.action()) {
            case TREAT_DISEASE -> {
                try {
                    Map<Color, List<Disease>> grouped = state.getPlayerRepository().getCurrentPlayer().getCity().getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
                    if (state.getPlayerRepository().getCurrentPlayer().getRole() instanceof Medic) {
                        var x = grouped.values().stream().max(Comparator.comparingInt(List::size)).orElse(null);
                        obj = new Object[]{requireNonNull(requireNonNull(x).get(0).getColor())};
                    } else {
                        var c = grouped.entrySet().stream().filter(list -> list.getValue().size() >= n).findFirst().orElse(null);
                        obj = new Object[]{requireNonNull(c).getKey()};
                    }
                } catch (NullPointerException noDiseases) {
                    System.err.println(noDiseases.getMessage() + " :: " + action + " :: " + IReportable.REPORT_PATH[0]);
                    //TODO: fix not allowed actions here
                    executedAction = ActionType.SKIP_ACTION;
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
                for (Color c : cards.keySet()) {   //check on the player cards the color to set as a parameter
                    if (cards.get(c).size() >= Cd(p)) obj = new Object[]{
                            state.getDiseaseRepository().getCures().get(c)
                    };
                }
            }
        }
        state.getPlayerRepository().playerAction(executedAction, state, obj == null ? new Object[]{} : obj);
        return treated;
    }

}
