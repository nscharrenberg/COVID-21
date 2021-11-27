package org.um.nine.headless.agents.utils.MacroActions;

import org.um.nine.headless.agents.utils.actions.MovingAction;
import org.um.nine.headless.agents.utils.actions.StandingAction;

import java.util.List;
import java.util.stream.Collectors;

public interface MacroAction {
    List<MovingAction> movingActions();
    List<StandingAction> standingActions();
    static MacroAction macro (List<MovingAction> ma, List<StandingAction> sa) {
        return new MacroAction() {
            @Override public List<MovingAction> movingActions() {return ma;}
            @Override public List<StandingAction> standingActions() {return sa;}
        };
    }

    default MacroAction add(MovingAction action){
        movingActions().add(action);
        return this;
    }

    default MacroAction add(StandingAction action){
        standingActions().add(action);
        return this;
    }

    record TreatDiseaseMacro(List<MovingAction> movingActions, List<StandingAction> standingActions) implements MacroAction {
        @Override
        public String toString() {
             String s = "";
             s += standingActions().stream().map(StandingAction::toString).collect(Collectors.toList());
             s += movingActions().stream().map(MovingAction::toString).collect(Collectors.toList());
             return s;
        }
    }
}
