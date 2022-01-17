package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.exceptions.GameOverException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.Cd;

public record MacroActionsExecutor() {

    public void executeIndexedMacro(IState state, MacroAction macro, boolean draw) throws GameOverException, Exception {
        state.getPlayerRepository().setCurrentRoundState(null);
        //System.out.println("Executing macro : "+macro);
        char[] index = macro.index().toCharArray();
        int m, s = m = 0;
        for (char c : index) {
            if (c == 'm') {
                executeMovingAction(state, macro.movingActions().get(m));
                m++;
            } else if (c == 's') {
                executeStandingAction(state, macro.standingActions().get(s));
                s++;
            }
        }
        if (draw) {
            state.getPlayerRepository().setCurrentRoundState(RoundState.DRAW);
            state.getPlayerRepository().playerAction(null, state);
        }
    }

    public void executeMovingAction(IState state, ActionType.MovingAction action) throws Exception {
        //System.out.println("Executing standing action "+action);
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.toCity().getName()));
        state.getPlayerRepository().playerAction(action.action(), state);
    }

    public void executeStandingAction(IState state, ActionType.StandingAction action) throws Exception {
        //System.out.println("Executing moving action "+action);
        state.getBoardRepository().setSelectedCity(state.getCityRepository().getCities().get(action.applyTo().getName()));
        Object[] obj = null;
        ActionType executedAction = action.action();
        switch (action.action()) {
            case TREAT_DISEASE -> {
                Map<Color, List<Disease>> grouped = state.getPlayerRepository().getCurrentPlayer().getCity().getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
                var x = grouped.values().stream().max(Comparator.comparingInt(List::size)).orElse(null);

                try {
                    obj = new Object[]{requireNonNull(requireNonNull(x).get(0).getColor())};
                } catch (NullPointerException noDiseases) {
                    executedAction = ActionType.SKIP_ACTION;
                }

            }
            case SHARE_KNOWLEDGE -> {
                Player sharingWith = state.getPlayerRepository().getPlayers().values().stream().filter(player -> player.getCity().equals(action.applyTo()) && !player.equals(state.getPlayerRepository().getCurrentPlayer())).findFirst().orElseThrow();
                CityCard toShare = state.getPlayerRepository().getCurrentPlayer().getHand().stream().map(pc -> (CityCard) pc).filter(cc -> cc.getCity().equals(action.applyTo()) && cc.getCity().equals(state.getPlayerRepository().getCurrentPlayer().getCity())).findFirst().orElseThrow();

                obj = new Object[]{sharingWith, toShare};
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
    }

}
