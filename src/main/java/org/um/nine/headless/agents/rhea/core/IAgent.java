package org.um.nine.headless.agents.rhea.core;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.agents.rhea.state.IState;

public interface IAgent extends Cloneable {
    MacroAction getNextMacroAction(IState state);
}
