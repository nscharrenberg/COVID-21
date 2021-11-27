package org.um.nine.headless.agents.utils.MacroActions;

import org.um.nine.headless.agents.utils.actions.MovingAction;
import org.um.nine.headless.agents.utils.actions.StandingAction;

import java.util.List;

public interface MacroAction {
    List<MovingAction> movingActions();
    List<StandingAction> standingActions();
}
