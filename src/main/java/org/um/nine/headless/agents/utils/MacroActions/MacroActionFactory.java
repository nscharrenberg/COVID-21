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
    protected static PathFinder.Descriptor pf;
    protected static IState state;
    private MacroActionFactory() {}
    public static void init(IState state){
        pf = new PathFinder.Descriptor(MacroActionFactory.state = state);
    }


    public static List<MacroAction> buildMacroActions(IState state, MacroType type) {
        TreatDiseaseMacroFactory treatDiseaseMacroFactory = new TreatDiseaseMacroFactory();
        switch (type) {
            case Treat1 -> {return treatDiseaseMacroFactory.treat1().buildTreatDiseaseMacroActions();}
            case Treat2 -> {return treatDiseaseMacroFactory.treat2().buildTreatDiseaseMacroActions();}
            case Treat3 -> {return treatDiseaseMacroFactory.treat3().buildTreatDiseaseMacroActions();}
            default -> {return new ArrayList<>();}
        }
    }


    private static class TreatDiseaseMacroFactory extends MacroActionFactory {
        private int treats = 3;
        public TreatDiseaseMacroFactory treat3() {this.treats = 3;return this;}
        public TreatDiseaseMacroFactory treat2() {this.treats = 2;return this;}
        public TreatDiseaseMacroFactory treat1() {this.treats = 1;return this;}

        public List<MacroAction> buildTreatDiseaseMacroActions() {
            var treatDiseaseMacroActionList = new ArrayList<MacroAction>();
            for (Map.Entry<String, City> sc : state.getCityRepository().getCities().entrySet()){
                List<MovingAction> shortestPath = pf.shortestPath(sc.getValue());
                int cubes = sc.getValue().getCubes().size();
                int movingActions = shortestPath.size();
                if (cubes >= 1 && movingActions <= 3){
                    int treats = 0;
                    int availableMoves = 4 - movingActions;
                    List<StandingAction> sa = new ArrayList<>();
                    while(treats < 3 && treats < this.treats){
                        var medic = treats > 0 && state.getPlayerRepository().getCurrentPlayer().getRole() instanceof Medic;
                        if (availableMoves > treats && cubes > treats && !medic)
                            sa.add(new StandingAction(TREAT_DISEASE, sc.getValue()));
                        treats++;
                    }
                    treatDiseaseMacroActionList.add(new MacroAction.TreatDiseaseMacro(shortestPath,sa));
                }
            }
            return treatDiseaseMacroActionList;
        }
    }

    public enum MacroType {
        Treat1,
        Treat2,
        Treat3
    }

}
