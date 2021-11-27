package org.um.nine.headless.agents.utils.MacroActions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.actions.PathFinder;

import java.util.ArrayList;
import java.util.List;

public abstract class MacroActionFactory {
    private final IState state;
    protected final PathFinder.Descriptor pathFinder;
    private final List<MacroAction> macroActions = new ArrayList<>();

    private MacroActionFactory(IState state) {
        this.state = state;
        this.pathFinder = new PathFinder.Descriptor(this.state);
    }

    private void buildActions(){
        this.macroActions.addAll(
                TreatDiseaseMacro.buildTreatDiseaseMacroActions(this.pathFinder, this.state)
        );


    }

    public List<MacroAction> getMacroActions() {
        return macroActions;
    }
}
