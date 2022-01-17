package org.um.nine.headless.agents.rhea.experiments;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.game.domain.Player;

public record MacroNode(Player player, MacroAction macroAction) {
}
