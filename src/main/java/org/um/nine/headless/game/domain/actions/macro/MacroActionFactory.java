package org.um.nine.headless.game.domain.actions.macro;

import org.um.nine.headless.agents.rhea.StateEvaluation;
import org.um.nine.headless.agents.utils.PathFinder;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.actions.ActionType;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.state.IState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.domain.actions.ActionType.*;

public abstract class MacroActionFactory {

    private static PathFinder.Descriptor pathFinder;
    private static IState state;
    private static MacroActionFactory instance;
    private static List<MacroAction> actions;
    private MacroActionFactory() {}
    private void addList(List<MacroAction> toAdd, List<MacroAction> actions) {
        int remaining = Settings.ROLLING_HORIZON - (actions.size() + toAdd.size());
        if (remaining >= 0) actions.addAll(toAdd);
        else actions.addAll(toAdd.subList(0, Settings.ROLLING_HORIZON - actions.size()));
    }
    private List<MacroAction> buildAllMacroActions () {
        List<MacroAction> actions = buildTreatDiseaseMacroActions(1);
        actions.addAll(buildTreatDiseaseMacroActions(2));
        actions.addAll(buildTreatDiseaseMacroActions(3));
        actions.addAll(buildCureDiseaseMacroActions());
        actions.addAll(buildResearchStationMacroActions());
        actions.addAll(buildWalkAwayMacroActions(4));
        return actions;
    }

    private List<MacroAction> buildMacroActions (MacroType type) {
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
    private List<MacroAction> buildWalkAwayMacroActions(int N) {
        List<MacroAction> actions = new ArrayList<>();
        for (City c : state.getCityRepository().getCities().values()){
            List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
            if (shortestPath.size() == N)
                actions.add(MacroAction.macro(shortestPath,
                        List.of(new ActionType.StandingAction(NO_ACTION,c))));
        }
        return actions;
    }
    private List<MacroAction> buildResearchStationMacroActions() {
        List<MacroAction> actions = new ArrayList<>();
        if (state.getCityRepository().getResearchStations().size() >= Settings.RESEARCH_STATION_THRESHOLD) return actions;
        for (PlayerCard pc : state.getPlayerRepository().getCurrentPlayer().getHand()){
            if (pc instanceof CityCard cc) {
                if (cc.getCity().getResearchStation() != null) continue;
                List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(cc.getCity());
                if (cc.getCity().equals(state.getPlayerRepository().getCurrentPlayer().getCity()))
                    shortestPath = new ArrayList<>();

                if (shortestPath.size() > 0 && shortestPath.size()<4) {
                    actions.add(MacroAction.macro(shortestPath,List.of(
                            new ActionType.StandingAction(BUILD_RESEARCH_STATION,cc.getCity()))));
                }

            }
        }
        return actions;
    }
    private List<MacroAction> buildCureDiseaseMacroActions() {
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
                    List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
                    if (shortestPath.size()<=3 && shortestPath.size()>0)
                        curingActions.add( MacroAction.macro(
                                shortestPath, List.of(new ActionType.StandingAction(DISCOVER_CURE,c))
                        ));
                }
            }
        }
        return curingActions;
    }
    private List<MacroAction> buildTreatDiseaseMacroActions(int t) {
        var treatDiseaseMacroActionList = new ArrayList<MacroAction>();
        for (Map.Entry<String, City> sc : state.getCityRepository().getCities().entrySet()){
            List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(sc.getValue());
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
                    List<ActionType.StandingAction> standingActions = new ArrayList<>();
                    while(treats < t) {
                        var medic = treats > 0 && state.getPlayerRepository().
                                getCurrentPlayer().getRole() instanceof Medic;

                        if (availableMoves > treats && cubes > treats && !medic)
                            //if medic add only one treat disease action no matter how many cubes
                            standingActions.add(new ActionType.StandingAction(TREAT_DISEASE, sc.getValue()));
                        treats++;
                    }
                    if (standingActions.size() == t)
                        treatDiseaseMacroActionList.add(new MacroAction.TreatDiseaseMacro(shortestPath,standingActions));
                }
            }

        }
        return treatDiseaseMacroActionList;
    }
    private List<MacroAction> buildHPAMacroActions() {
        actions = new ArrayList<>();
        var cure = buildCureDiseaseMacroActions();
        this.addList(cure,actions);
        var treat3 = buildTreatDiseaseMacroActions(3);
        this.addList(treat3,actions);
        var build  = buildResearchStationMacroActions();
        this.addList(build,actions);
        var treat2 = buildTreatDiseaseMacroActions(2);
        this.addList(treat2,actions);
        var treat1 = buildTreatDiseaseMacroActions(1);
        this.addList(treat1,actions);
        var walk = buildWalkAwayMacroActions(state.getPlayerRepository().getActionsLeft());
        this.addList(walk,actions);
        return actions;
    }

    private static MacroActionFactory getInstance() {
        if (instance == null) instance = new MacroActionFactory(){};
        return instance;
    }
    private static void reset(){
        actions = null;
        pathFinder = null;
        state = null;
    }

    public static MacroAction findSuitableMacro(int remainingActions){
        City current = state.getPlayerRepository().getCurrentPlayer().getCity();
        if (remainingActions == 1){
            if (current.getCubes().size() >= 1){
                    return MacroAction.macro(new ArrayList<>(),
                            List.of(new StandingAction(TREAT_DISEASE,current)));
            }
            City n = current.getNeighbors().get(0);
            return MacroAction.macro(List.of(
                    new MovingAction(DRIVE,current,n)
            ), new ArrayList<>());
        }
        if (remainingActions == 2){
            if (current.getCubes().size() >= 2){
                return MacroAction.macro(new ArrayList<>(),
                        List.of(new StandingAction(TREAT_DISEASE,current),
                                new StandingAction(TREAT_DISEASE,current)));
            }
            for (City c : current.getNeighbors()){
                if (c.getCubes().size()>=1){
                    return MacroAction.macro(
                            List.of(new MovingAction(
                                    DRIVE,state.getPlayerRepository().getCurrentPlayer().getCity(),c)),
                            List.of(new StandingAction(TREAT_DISEASE,c)));
                }
            }
            City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
            return MacroAction.macro(List.of(
                    new MovingAction(DRIVE,current,n),
                    new MovingAction(DRIVE,n,nn)
            ), new ArrayList<>());
        }
        if (remainingActions == 3) {
            if (current.getCubes().size() >= 3){
                return MacroAction.macro(new ArrayList<>(),
                        List.of(new StandingAction(TREAT_DISEASE,current),
                                new StandingAction(TREAT_DISEASE,current),
                                new StandingAction(TREAT_DISEASE,current)));
            }
            City save = null;
            for (City c : current.getNeighbors()){
                if (c.getCubes().size() == 1) save =c;
                if (c.getCubes().size()>=2){
                    return MacroAction.macro(
                            List.of(new MovingAction(
                                    DRIVE,state.getPlayerRepository().getCurrentPlayer().getCity(),c)),
                            List.of(new StandingAction(TREAT_DISEASE,c),
                                    new StandingAction(TREAT_DISEASE,c)));
                }
            }
            if (save!= null){
                return MacroAction.macro(
                        List.of(new MovingAction(
                                DRIVE,state.getPlayerRepository().getCurrentPlayer().getCity(),save)),
                        List.of(new StandingAction(TREAT_DISEASE,save),
                                new StandingAction(TREAT_DISEASE,save)));
            }
            City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
            return MacroAction.macro(List.of(
                            new MovingAction(DRIVE,current,n),
                            new MovingAction(DRIVE,n,nn),
                            new MovingAction(DRIVE,nn,nn.getNeighbors().get(0))
            ) , new ArrayList<>());
        }
        throw new IllegalStateException();
    }
    public static MacroAction getFirstMacros(IState state) {
        init(state);
        MacroAction macro = getInstance().getActions().get(0);
        getInstance().getActions().remove(macro);
        return macro;
    }

    public static MacroActionFactory init(IState newState) {
        reset();
        state = newState;
        pathFinder = new PathFinder.Descriptor(state);
        return getInstance();
    }

    public List<MacroAction> getActions() {
        return actions == null ? (actions = buildHPAMacroActions()) : actions;
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
