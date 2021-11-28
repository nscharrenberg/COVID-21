package org.um.nine.headless.agents.utils.MacroActions;

import org.um.nine.headless.agents.rhea.StateEvaluation;
import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.agents.utils.actions.MovingAction;
import org.um.nine.headless.agents.utils.actions.PathFinder;
import org.um.nine.headless.agents.utils.actions.StandingAction;
import org.um.nine.headless.game.Info;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.domain.ActionType.*;

public abstract class MacroActionFactory {
    private static PathFinder.Descriptor pathFinder;
    private static IState state;

    private MacroActionFactory() {}

    public static void init(IState state) {
        pathFinder = new PathFinder.Descriptor(MacroActionFactory.state = state);
    }
    public static List<MacroAction> buildHPAMacroActions (int H) {
        var actions = new ArrayList<MacroAction>(H);
        var cure = buildCureDiseaseMacroActions();
        addList(cure,actions,H);
        var treat3 = buildTreatDiseaseMacroActions(3);
        addList(treat3,actions,H);
        var build  = buildResearchStationMacroActions();
        addList(build,actions,H);
        var treat2 = buildTreatDiseaseMacroActions(2);
        addList(treat2,actions,H);
        var treat1 = buildTreatDiseaseMacroActions(1);
        addList(treat1,actions,H);
        var walk = buildWalkAwayMacroActions(state.getPlayerRepository().getActionsLeft());
        addList(walk,actions,H);
        return actions;
    }
    private static void addList (List<MacroAction> toAdd, List<MacroAction> actions, int H){
        int remaining = H - (actions.size() + toAdd.size());
        if (remaining >= 0) actions.addAll(toAdd);
        else actions.addAll(toAdd.subList(0, H - actions.size()));
    }
    public static List<MacroAction> buildAllMacroActions () {
        List<MacroAction> actions = buildTreatDiseaseMacroActions(1);
        //actions.addAll(buildTreatDiseaseMacroActions(2));
        //actions.addAll(buildTreatDiseaseMacroActions(3));
        actions.addAll(buildCureDiseaseMacroActions());
        actions.addAll(buildResearchStationMacroActions());
        actions.addAll(buildWalkAwayMacroActions(4));
        return actions;
    }
    public static List<MacroAction> buildMacroActions (MacroType type) {
        return switch (type) {
            case Treat1Macro -> buildTreatDiseaseMacroActions(1);
            case Treat2Macro -> buildTreatDiseaseMacroActions(2);
            case Treat3Macro -> buildTreatDiseaseMacroActions(3);
            case CureMacro -> buildCureDiseaseMacroActions();
            case ResearchStationMacro -> buildResearchStationMacroActions();
            case WalkAway1Macro -> buildWalkAwayMacroActions(1);
            case WalkAway2Macro -> buildWalkAwayMacroActions(2);
            case WalkAway3Macro -> buildWalkAwayMacroActions(3);
            case WalkAway4Macro -> buildWalkAwayMacroActions(4);
        };
    }
    public static List<MacroAction> buildWalkAwayMacroActions(int N) {
        List<MacroAction> actions = new ArrayList<>();
        for (City c : state.getCityRepository().getCities().values()){
            List<MovingAction> shortestPath = pathFinder.shortestPath(c);
            if (shortestPath.size() == N)
                actions.add(MacroAction.macro(shortestPath,
                        List.of(new StandingAction(NO_ACTION,c))));
        }
        return actions;
    }
    public static List<MacroAction> buildResearchStationMacroActions() {
        List<MacroAction> actions = new ArrayList<>();
        if (state.getCityRepository().getResearchStations().size() >= Info.RESEARCH_STATION_THRESHOLD) return actions;
        for (PlayerCard pc : state.getPlayerRepository().getCurrentPlayer().getHand()){
            if (pc instanceof CityCard cc) {
                if (cc.getCity().getResearchStation()!= null) continue;
                List<MovingAction> shortestPath = pathFinder.shortestPath(cc.getCity());
                if (cc.getCity().equals(state.getPlayerRepository().getCurrentPlayer().getCity()))
                    shortestPath = new ArrayList<>();

                if (shortestPath.size() > 0 && shortestPath.size()<4) {
                    actions.add(MacroAction.macro(shortestPath,List.of(
                            new StandingAction(BUILD_RESEARCH_STATION,cc.getCity()))));
                }

            }
        }
        return actions;
    }
    public static List<MacroAction> buildCureDiseaseMacroActions() {
        List<MacroAction> curingActions = new ArrayList<>();

        Player current = state.getPlayerRepository().getCurrentPlayer();
        int needed = StateEvaluation.Cd(current);

        for (Cure cure : state.getDiseaseRepository().getCures().values()){
            if (cure.isDiscovered()) continue;
            Color color = cure.getColor();
            long inHand = current.getHand().stream().
                    filter(pc ->
                            pc instanceof CityCard cc && cc.getCity().getColor().equals(color)
                    ).count();
            if (inHand >= needed){
                List<City> stations = state.getCityRepository().
                        getResearchStations().
                        stream().
                        map(ResearchStation::getCity).
                        collect(Collectors.toList());
                for (City c : stations){
                    List<MovingAction> shortestPath = pathFinder.shortestPath(c);
                    if (shortestPath.size()<=3 && shortestPath.size()>0)
                        curingActions.add( MacroAction.macro(
                                shortestPath, List.of(new StandingAction(DISCOVER_CURE,c))
                        ));
                }
            }
        }
        return curingActions;
    }
    public static List<MacroAction> buildTreatDiseaseMacroActions(int t) {
        var treatDiseaseMacroActionList = new ArrayList<MacroAction>();
        for (Map.Entry<String, City> sc : state.getCityRepository().getCities().entrySet()){
            List<MovingAction> shortestPath = pathFinder.shortestPath(sc.getValue());
            if (shortestPath.size() == 0 &&   // if path is size 0
                    !sc.getValue().equals(state.getPlayerRepository().getCurrentPlayer().getCity()))
                //but we are not in that city
                continue;
            Map<Color, List<Disease>> grouped = sc.getValue().getCubes().stream().collect(
                    Collectors.groupingBy(Disease::getColor)
            );
            for (List<Disease> diseases : grouped.values()){
                int cubes = diseases.size();
                int movingActions = shortestPath.size();
                if (cubes >= 1 && movingActions <= 3){
                    int treats = 0;
                    int availableMoves = 4 - movingActions;
                    List<StandingAction> standingActions = new ArrayList<>();
                    while(treats < t) {
                        var medic = treats > 0 && state.getPlayerRepository().
                                getCurrentPlayer().getRole() instanceof Medic;

                        if (availableMoves > treats && cubes > treats && !medic)
                            //if medic add only one treat disease action no matter how many cubes
                            standingActions.add(new StandingAction(TREAT_DISEASE, sc.getValue()));
                        treats++;
                    }
                    if (standingActions.size() == t)
                        treatDiseaseMacroActionList.add(new MacroAction.TreatDiseaseMacro(shortestPath,standingActions));
                }
            }

        }
        return treatDiseaseMacroActionList;
    }

    public enum MacroType {
        Treat1Macro,
        Treat2Macro,
        Treat3Macro,
        CureMacro,
        ResearchStationMacro,
        WalkAway1Macro,
        WalkAway2Macro,
        WalkAway3Macro,
        WalkAway4Macro
    }

}
