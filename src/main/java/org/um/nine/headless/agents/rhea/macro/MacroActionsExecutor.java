package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.roles.Researcher;
import org.um.nine.headless.game.exceptions.InvalidMoveException;
import org.um.nine.headless.game.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.Cd;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.findMostValuableCityCardForPlayer;

public record MacroActionsExecutor() {

    public void forwardMacro(IState state, MacroAction macro) throws Exception {
        RoundState prev = state.getPlayerRepository().getCurrentRoundState();
        state.getPlayerRepository().setCurrentRoundState(RoundState.ACTION);
        char[] index = macro.index().toCharArray();
        int m, s = m = 0;
        for (char c : index) {
            if (c == 'm') {
                try {
                    executeMovingAction(state, macro.movingActions().get(m));
                } catch (InvalidMoveException e) {
                    System.out.println(e.getMessage());
                    System.out.println(macro);
                }
                m++;
            } else if (c == 's') {
                try {
                    executeStandingAction(state, macro.standingActions().get(s));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                    System.out.println(macro);
                }
                s++;
            }
        }
        state.getPlayerRepository().setCurrentRoundState(prev);
    }

    public void executeIndexedMacro(IState state, MacroAction macro, boolean draw) throws Exception {
        state.getPlayerRepository().resetRound();
        state.getPlayerRepository().setCurrentRoundState(RoundState.ACTION);
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
            try {
                state.getPlayerRepository().playerAction(null, state);
            } catch (NoDiseaseOrOutbreakPossibleDueToEvent ignored) {
            }
            state.getPlayerRepository().resetRound();
            state.getPlayerRepository().setCurrentRoundState(null);
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
        Player currentPlayer = state.getPlayerRepository().getCurrentPlayer();


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
                //this has to be the same when applying share knowledge: can only give card if in the same city
                Player sharingWith = action.applyTo().
                        getPawns().
                        stream().
                        filter(player -> !player.equals(currentPlayer)).
                        findFirst().
                        orElseThrow();

                CityCard toShare;

                if (currentPlayer.getRole() instanceof Researcher) {
                    toShare = findMostValuableCityCardForPlayer(currentPlayer, sharingWith);
                }
//                fixme : why am i stupid
//                  todo : give lozio some neurons
//                else if (sharingWith.getRole() instanceof Researcher) {
//                    toShare = findMostValuableCityCardForPlayer(sharingWith, currentPlayer);
//                }
                else {
                    //then if not current player can only share the city card with the city where it's in
                    toShare = currentPlayer.
                            getHand().
                            stream().
                            map(pc -> (CityCard) pc).
                            filter(cc -> cc.getCity().equals(action.applyTo()) &&
                                    cc.getCity().equals(currentPlayer.getCity()) &&
                                    cc.getCity().equals(sharingWith.getCity())).
                            findFirst().
                            orElseThrow();
                }


                obj = new Object[]{sharingWith, toShare};
            }

            case DISCOVER_CURE -> {
                var cards = currentPlayer.getHand().stream().collect(Collectors.groupingBy(c -> ((CityCard) c).getCity().getColor()));
                for (Color c : cards.keySet()) {   //check on the player cards the color to set as a parameter
                    if (cards.get(c).size() >= Cd(currentPlayer)) {
                        obj = new Object[]{
                                state.getDiseaseRepository().getCures().get(c)
                        };
                        break;
                    }

                }
            }
        }
        state.getPlayerRepository().playerAction(executedAction, state, obj == null ? new Object[]{} : obj);
    }

}
