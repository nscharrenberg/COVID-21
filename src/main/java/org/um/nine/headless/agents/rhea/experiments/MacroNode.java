package org.um.nine.headless.agents.rhea.experiments;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.Player;

public record MacroNode(Player player, MacroAction macroAction) {
    public ActionType[] allActions() {
        ActionType[] all = new ActionType[macroAction().size()];
        for (int i = 0; i < macroAction().size(); i++) {
            Record e = macroAction().getAtIndex(i);
            if (e instanceof ActionType.StandingAction sa) all[i] = sa.action();
            if (e instanceof ActionType.MovingAction ma) all[i] = ma.action();
        }
        return all;
    }

    public String toString() {
        return player().getName() + " -> " + macroAction();
    }
}
