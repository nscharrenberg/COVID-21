package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.um.nine.headless.game.Settings.DEFAULT_MACRO_ACTIONS_EXECUTOR;

public interface MacroAction extends Cloneable {
    List<ActionType.MovingAction> movingActions();

    List<ActionType.StandingAction> standingActions();

    String index();

    void setIndex(String s);


    default int size() {
        return movingActions().size() + standingActions().size();
    }

    default Record getAtIndex(int index) {
        int standingIndex = 0, movingIndex = 0;
        for (int i = 0; i < index().length(); i++) {
            if (i == index) {
                if (index().charAt(i) == 's') return standingActions().get(standingIndex);
                if (index().charAt(i) == 'm') return movingActions().get(movingIndex);
            }
            if (index().charAt(i) == 's') standingIndex++;
            else if (index().charAt(i) == 'm') movingIndex++;
        }
        return null;
    }

    default MacroAction add(ActionType.MovingAction action) {
        if (this.size() == 4) throw new IllegalArgumentException();
        this.setIndex(index() + "m");
        this.movingActions().add(action);
        return this;
    }

    default MacroAction add(ActionType.StandingAction action) {
        if (this.size() == 4) throw new IllegalArgumentException();
        setIndex(index() + "s");
        standingActions().add(action);
        return this;
    }

    default MacroAction clone() {
        ArrayList<ActionType.StandingAction> sa = this.standingActions().stream().map(ActionType.StandingAction::getClone).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ActionType.MovingAction> ma = this.movingActions().stream().map(ActionType.MovingAction::getClone).collect(Collectors.toCollection(ArrayList::new));
        MacroAction m = macro(ma, sa);
        m.setIndex(this.index());
        return m;
    }

    default boolean isSkipAction() {
        return movingActions().isEmpty() && standingActions().stream().allMatch(sa -> sa.action().equals(ActionType.SKIP_ACTION));
    }

    default MacroAction executableNow(IState currentState) {

        if (currentState.isGameLost())
            return skipMacroAction(4, currentState.getPlayerRepository().getCurrentPlayer().getCity());


        IState forwardState = currentState.clone();
        //start from the current city (there shouldn't be any error staying here)
        City currentCity = forwardState.getPlayerRepository().getCurrentPlayer().getCity();
        MacroAction executable = macro(new ArrayList<>(), new ArrayList<>()); //empty macro

        int actionsExecuted;
        int m, s = m = 0;
        forwardState.getPlayerRepository().setCurrentRoundState(null);

        for (char c : index().toCharArray()) {
            try {                    //try executing the macro
                if (c == 'm') {
                    DEFAULT_MACRO_ACTIONS_EXECUTOR.executeMovingAction(forwardState, movingActions().get(m));
                    // if no exceptions
                    currentCity = movingActions().get(m).toCity();  //update current city when moving
                    executable.add(movingActions().get(m));  // append it to executable
                    m++;  // update the executed actions count
                } else if (c == 's') {
                    DEFAULT_MACRO_ACTIONS_EXECUTOR.executeStandingAction(forwardState, standingActions().get(s));
                    // if no exceptions
                    executable.add(standingActions().get(s));
                    s++;
                }
            } catch (Exception ignored) {
                break;  // stop the forwarding process
            }
        }

        //count the actions which have been successfully applied
        actionsExecuted = s + m;
        for (int i = actionsExecuted; i < 4; i++)
            executable.add(new ActionType.StandingAction(ActionType.SKIP_ACTION, currentCity));

        return executable;
    }



    static String index(MacroAction m) {
        return "m".repeat(m.movingActions().size()) +
                "s".repeat(m.standingActions().size());
    }
    static String getDescription(MacroAction macro) {
        StringBuilder desc = new StringBuilder();
        int s = 0, m = 0;
        for (char c : macro.index().toCharArray()) {
            if (c == 's') {
                desc.append(macro.standingActions().get(s));
                s++;
            } else if (c == 'm') {
                desc.append(macro.movingActions().get(m));
                m++;
            }
        }
        return desc.toString();
    }

    static MacroAction combine(MacroAction ma1, MacroAction ma2) {
        if (ma1.size() + ma2.size() > 4) throw new IllegalArgumentException();
        for (ActionType.MovingAction m : ma2.movingActions()) ma1.add(m);
        for (ActionType.StandingAction s : ma2.standingActions()) ma1.add(s);
        return ma1;
    }

    static MacroAction combine(MacroAction... ms) {
        IntStream.range(1, ms.length).forEach(i -> combine(ms[0], ms[i]));
        return ms[0];
    }

    static MacroAction skipMacroAction(int size, City currentCity) {
        List<ActionType.StandingAction> skip = IntStream.range(0, size).
                mapToObj(i ->
                        new ActionType.StandingAction(
                                ActionType.SKIP_ACTION,
                                currentCity
                        ))
                .collect(Collectors.toList());
        return macro(new ArrayList<>(), skip);
    }


    static MacroAction macro(List<ActionType.MovingAction> ma, List<ActionType.StandingAction> sa) {
        return new MacroAction() {
            String index;

            @Override
            public MacroAction clone() {
                return MacroAction.super.clone();
            }

            @Override
            public List<ActionType.MovingAction> movingActions() {
                return ma;
            }

            @Override
            public List<ActionType.StandingAction> standingActions() {
                return sa;
            }

            @Override
            public String index() {
                return index == null ? (index = MacroAction.index(this)) : index;
            }

            @Override
            public void setIndex(String s) {
                index = s;
            }
            @Override
            public String toString() {
                return getDescription(this);
            }
        };
    }
    class TreatDiseaseMacro implements MacroAction {

        private final List<ActionType.MovingAction> movingActions;
        private final List<ActionType.StandingAction> standingActions;
        private String index;

        public TreatDiseaseMacro(List<ActionType.MovingAction> movingActions, List<ActionType.StandingAction> standingActions) {
            this.movingActions = movingActions;
            this.standingActions = standingActions;
        }

        @Override
        public String toString() {
            return getDescription(this);
        }

        @Override
        public List<ActionType.MovingAction> movingActions() {
            return this.movingActions;
        }

        @Override
        public List<ActionType.StandingAction> standingActions() {
            return this.standingActions;
        }

        @Override
        public String index() {
            return index == null ? (index = MacroAction.index(this)) : index;
        }

        @Override
        public void setIndex(String s) {
            this.index = s;
        }

        @Override
        public MacroAction clone() {
            return MacroAction.super.clone();
        }
    }
}
