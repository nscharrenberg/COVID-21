package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.game.domain.ActionType;

import java.util.List;
import java.util.stream.Collectors;

public interface MacroAction {
    List<ActionType.MovingAction> movingActions();

    List<ActionType.StandingAction> standingActions();

    String index();

    void setIndex(String s);

    static void combine(MacroAction ma1, MacroAction ma2) {
        for (ActionType.MovingAction m : ma2.movingActions()) ma1.add(m);
        for (ActionType.StandingAction s : ma2.standingActions()) ma1.add(s);
    }

    static void combine(MacroAction... ms) {
        for (MacroAction m : ms) combine(ms[0], m);
    }

    static String index(MacroAction m) {
        return "m".repeat(m.movingActions().size()) +
                "s".repeat(m.standingActions().size());
    }

    static MacroAction macro(List<ActionType.MovingAction> ma, List<ActionType.StandingAction> sa) {
        return new MacroAction() {
            String index;

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
                String s = "";
                s += standingActions().stream().map(ActionType.StandingAction::toString).collect(Collectors.toList());
                s += "\t\t" + movingActions().stream().map(ActionType.MovingAction::toString).collect(Collectors.toList());
                return s;
            }
        };
    }

    default MacroAction add(ActionType.MovingAction action) {
        setIndex(index() + "m");
        movingActions().add(action);
        return this;
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

    default MacroAction add(ActionType.StandingAction action) {
        setIndex(index() + "s");
        standingActions().add(action);
        return this;
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
            String s = "";
            s += standingActions().stream().map(ActionType.StandingAction::toString).collect(Collectors.toList());
            s += "\t\t" + movingActions().stream().map(ActionType.MovingAction::toString).collect(Collectors.toList());
            return s;
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
    }
}
