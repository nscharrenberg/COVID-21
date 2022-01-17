package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.game.domain.ActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface MacroAction extends Cloneable {
    List<ActionType.MovingAction> movingActions();

    List<ActionType.StandingAction> standingActions();

    String index();

    void setIndex(String s);

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

    default MacroAction clone() {
        ArrayList<ActionType.StandingAction> sa = standingActions().stream().map(ActionType.StandingAction::getClone).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ActionType.MovingAction> ma = movingActions().stream().map(ActionType.MovingAction::getClone).collect(Collectors.toCollection(ArrayList::new));
        MacroAction m = macro(ma, sa);
        m.setIndex(index());
        return m;
    }

    default int size() {
        return movingActions().size() + standingActions().size();
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
