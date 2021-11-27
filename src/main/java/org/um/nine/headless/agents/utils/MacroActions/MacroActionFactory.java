package org.um.nine.headless.agents.utils.MacroActions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.actions.MovingAction;
import org.um.nine.headless.agents.utils.actions.PathFinder;
import org.um.nine.headless.agents.utils.actions.StandingAction;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.roles.Medic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.um.nine.headless.game.domain.ActionType.TREAT_DISEASE;

public abstract class MacroActionFactory {
    private static final List<MacroAction> macroActions = new ArrayList<>();
    private MacroActionFactory() {}
    public static void evaluateState (IState state) {
        macroActions.clear();
        macroActions.addAll(new TreatDiseaseMacroFactory().buildMacroActions(state)
        );
    }

    abstract List<MacroAction> buildMacroActions(IState state);

    public static List<MacroAction> getMacroActions() {
        return macroActions;
    }

    private static class TreatDiseaseMacroFactory extends MacroActionFactory {
        public List<MacroAction> buildMacroActions(IState state) {
            PathFinder.Descriptor pf = new PathFinder.Descriptor(state);

            var treatDiseaseMacroActionList = new ArrayList<MacroAction>();
            for (Map.Entry<String, City> sc : state.getCityRepository().getCities().entrySet()){
                List<MovingAction> shortestPath = pf.shortestPath(sc.getValue());
                int cubes = sc.getValue().getCubes().size();
                int movingActions = shortestPath.size();
                if (cubes >= 1 && movingActions <= 3){
                    int treats = 0;
                    int availableMoves = 4 - movingActions;
                    List<StandingAction> sa = new ArrayList<>();
                    while(treats < 3){
                        var medic = treats > 0 && state.getPlayerRepository().getCurrentPlayer().getRole() instanceof Medic;
                        if (availableMoves > treats && cubes > treats && !medic) {
                            sa.add(new StandingAction(TREAT_DISEASE, sc.getValue()));
                        }
                        treats++;
                    }
                    treatDiseaseMacroActionList.add(new MacroAction.TreatDiseaseMacro(shortestPath,sa));
                }
            }
            return treatDiseaseMacroActionList;
        }
    }
}
