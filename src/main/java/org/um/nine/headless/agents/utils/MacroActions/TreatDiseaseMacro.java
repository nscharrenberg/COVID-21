package org.um.nine.headless.agents.utils.MacroActions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.actions.MovingAction;
import org.um.nine.headless.agents.utils.actions.PathFinder;
import org.um.nine.headless.game.domain.City;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreatDiseaseMacro {

    private static final List<MacroAction> treatDiseaseMacroActionList = new ArrayList<>();
    public static List<MacroAction> buildTreatDiseaseMacroActions(PathFinder.Descriptor pathFinder, IState state) {
        treatDiseaseMacroActionList.clear();
        List<City> closeByCitiesWithDiseases = state.getCityRepository().
                getCities().values().stream().
                filter(city -> city.getCubes().size()>=1).
                filter(city -> pathFinder.shortestPath(city).size()<=3).
                collect(Collectors.toList());

        for (City c : closeByCitiesWithDiseases){
            List<MovingAction> path = pathFinder.shortestPath(c);
        }

        return treatDiseaseMacroActionList;
    }

}
