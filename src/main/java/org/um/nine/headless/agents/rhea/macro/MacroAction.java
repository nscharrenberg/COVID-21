package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.game.domain.ActionType;

import java.util.List;
import java.util.stream.Collectors;

public interface MacroAction {
    List<ActionType.MovingAction> movingActions();
    List<ActionType.StandingAction> standingActions();
    static MacroAction macro (List<ActionType.MovingAction> ma, List<ActionType.StandingAction> sa) {
        return new MacroAction() {
            @Override public List<ActionType.MovingAction> movingActions() {return ma;}
            @Override public List<ActionType.StandingAction> standingActions() {return sa;}
            @Override public String toString() {
                String s = "";
                s += standingActions().stream().map(ActionType.StandingAction::toString).collect(Collectors.toList());
                s += "\t\t" + movingActions().stream().map(ActionType.MovingAction::toString).collect(Collectors.toList());
                return s;
            }
        };
    }

    default MacroAction add(ActionType.MovingAction action){
        movingActions().add(action);
        return this;
    }

    default MacroAction add(ActionType.StandingAction action){
        standingActions().add(action);
        return this;
    }

    record TreatDiseaseMacro(List<ActionType.MovingAction> movingActions, List<ActionType.StandingAction> standingActions) implements MacroAction {
        @Override
        public String toString() {
             String s = "";
             s += standingActions().stream().map(ActionType.StandingAction::toString).collect(Collectors.toList());
             s += "\t\t" + movingActions().stream().map(ActionType.MovingAction::toString).collect(Collectors.toList());
             return s;
        }
    }
}
