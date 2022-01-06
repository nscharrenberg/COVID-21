package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.state.StateEvaluation;
import org.um.nine.headless.agents.utils.pathfinder.PathFinder;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;

import javax.crypto.Mac;
import java.util.*;
import java.util.stream.Collectors;

import static org.um.nine.headless.game.domain.ActionType.*;

public abstract class MacroActionFactory {

    protected static PathFinder.Descriptor pathFinder;
    protected static IState state;
    protected static MacroActionFactory instance;
    protected static List<MacroAction> actions;
    protected static Player currentPlayer;

    protected MacroActionFactory() {
    }

    protected static void addList(List<MacroAction> toAdd, List<MacroAction> actions) {
        int remaining = Settings.ROLLING_HORIZON - (actions.size() + toAdd.size());
        if (remaining >= 0) actions.addAll(toAdd);
        else actions.addAll(toAdd.subList(0, Settings.ROLLING_HORIZON - actions.size()));
    }

    protected static List<MacroAction> buildAllMacroActions() {
        List<MacroAction> actions = buildTreatDiseaseMacroActions(1);
        actions.addAll(buildTreatDiseaseMacroActions(2));
        actions.addAll(buildTreatDiseaseMacroActions(3));
        actions.addAll(buildCureDiseaseMacroActions());
        actions.addAll(buildResearchStationMacroActions());
        actions.addAll(buildWalkAwayMacroActions(4));
        return actions;
    }

    protected static List<MacroAction> buildMacroActions(MacroType type) {
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
            case ShareKnowledgeMacro -> buildGiveKnowledgeMacroActions();
        };
    }

    protected static List<MacroAction> buildWalkAwayMacroActions(int N) {
        List<MacroAction> actions = new ArrayList<>();
        for (City c : state.getCityRepository().getCities().values()) {
            List<MovingAction> shortestPath = pathFinder.shortestPath(c);
            List<StandingAction> s = new ArrayList<>();
            if (shortestPath.size() <= N) {

                for (int i = 0; i < N - shortestPath.size(); i++)
                    s.add(new ActionType.StandingAction(NO_ACTION, c,null,null));

                actions.add(MacroAction.macro(shortestPath, s));
            }
        }
        return actions.stream().sorted(Comparator.comparingInt(ma -> ((MacroAction) ma).movingActions().size()).reversed()).collect(Collectors.toList());
    }

    protected static List<MacroAction> buildResearchStationMacroActions() {
        List<MacroAction> actions = new ArrayList<>();
        if (state.getCityRepository().getResearchStations().size() >= Settings.RESEARCH_STATION_THRESHOLD)
            return actions;
        for (PlayerCard pc : getCurrentPlayer().getHand()) {
            if (pc instanceof CityCard cc) {
                if (cc.getCity().getResearchStation() != null) continue;
                List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(cc.getCity());
                if (cc.getCity().equals(getCurrentPlayer().getCity()))
                    shortestPath = new ArrayList<>();

                if (shortestPath.size() > 0 && shortestPath.size() < 4) {
                    List<StandingAction> s = new ArrayList<>();
                    s.add(new ActionType.StandingAction(BUILD_RESEARCH_STATION, cc.getCity(),null,null));
                    actions.add(MacroAction.macro(shortestPath, s));
                }

            }
        }
        return actions;
    }

    protected static List<MacroAction> buildCureDiseaseMacroActions() {
        List<MacroAction> curingActions = new ArrayList<>();

        Player current = getCurrentPlayer();
        int needed = StateEvaluation.Cd(current);

        for (Cure cure : state.getDiseaseRepository().getCures().values()) {
            if (cure.isDiscovered()) continue;
            Color color = cure.getColor();
            long inHand = current.getHand().stream().
                    filter(pc ->
                            pc instanceof CityCard cc && cc.getCity().getColor().equals(color)
                    ).count();
            if (inHand >= needed) {
                List<City> stations = state.getCityRepository().
                        getResearchStations().
                        stream().
                        map(ResearchStation::getCity).
                        collect(Collectors.toList());
                for (City c : stations) {
                    List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
                    if (shortestPath.size() <= 3 && shortestPath.size() > 0)
                        curingActions.add(MacroAction.macro(
                                shortestPath, List.of(new ActionType.StandingAction(DISCOVER_CURE, c,null,null))
                        ));
                }
            }
        }
        return curingActions;
    }

    protected static List<MacroAction> buildGiveKnowledgeMacroActions() {
        List<MacroAction> shareKnowledgeActions = new ArrayList<>();

        List<PlayerCard> cardsInHand = currentPlayer.getHand();
        List<City> citiesInHand = new ArrayList<>();
        for (PlayerCard card : cardsInHand) {//TODO: Make sure shortestPath check is correct
            if (card instanceof CityCard && pathFinder.shortestPath(((CityCard) card).getCity()).size() <= 4)
                citiesInHand.add(((CityCard) card).getCity());
        }

        List<Player> otherPlayers = new ArrayList<>();
        for (var entry : state.getPlayerRepository().getPlayers().entrySet()) {
            if (entry != currentPlayer) otherPlayers.add(entry.getValue());
        }

        List<Player> playersInCities = new ArrayList<>();
        for (Player p : otherPlayers) {
            for (City c : citiesInHand) {
                if (c == p.getCity()) playersInCities.add(p);
            }
        }


        for (Player p: playersInCities) {
            City c = p.getCity();
            shareKnowledgeActions.add(MacroAction.macro(pathFinder.shortestPath(c), List.of(new ActionType.StandingAction(SHARE_KNOWLEDGE,c,currentPlayer,p))));
        }
        //TODO: add "wait in city" actions
        //citiesInHand contains cities that currentPlayer can reach in one turn (includes the cities where there are other players)
        return shareKnowledgeActions;
    }

    protected static List<MacroAction> buildTakeKnowledgeMacroAction() {
        List<MacroAction> shareKnowledgeActions = new ArrayList<>();
        List<City> citiesInRange = new ArrayList<>();
        List<Player> playersInReachableCities = new ArrayList<>();

        List<Player> otherPlayers = new ArrayList<>();
        for (var entry : state.getPlayerRepository().getPlayers().entrySet()) {
            if (entry != currentPlayer) otherPlayers.add(entry.getValue());
        }

        for (Player p : otherPlayers) {
            List<PlayerCard> cardsInHand = p.getHand();
            for (PlayerCard card : cardsInHand) {//TODO: Make sure shortestPath check is correct
                if (card instanceof CityCard && pathFinder.shortestPath(((CityCard) card).getCity()).size() <= 4) {
                    City c = ((CityCard) card).getCity();
                    citiesInRange.add(c);
                    if(c==p.getCity()) playersInReachableCities.add(p);
                }
            }
        }

        for (Player p: playersInReachableCities) {
            City c = p.getCity();
            shareKnowledgeActions.add(MacroAction.macro(pathFinder.shortestPath(c), List.of(new ActionType.StandingAction(SHARE_KNOWLEDGE,c,p,currentPlayer))));
        }
        //TODO: add "wait here" actions
        //citiesInRange contains cities that currentPlayer can reach in one turn (includes the cities where there are other players)
        return shareKnowledgeActions;
    }


    protected static List<MacroAction> buildTreatDiseaseMacroActions() {
        var treat3 = buildTreatDiseaseMacroActions(3);
        if (treat3.size() > 0) return treat3;
        var treat2 = buildTreatDiseaseMacroActions(2);
        if (treat2.size() > 0) return treat2;
        var treat1 = buildTreatDiseaseMacroActions(1);
        if (treat1.size() > 0) return treat1;
        return new ArrayList<>();
    }

    protected static List<MacroAction> buildTreatDiseaseMacroActions(int t) {
        var treatDiseaseMacroActionList = new ArrayList<MacroAction>();
        for (Map.Entry<String, City> sc : state.getCityRepository().getCities().entrySet()) {
            List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(sc.getValue());
            if (shortestPath.size() == 0 &&   // if path is size 0
                    !sc.getValue().equals(getCurrentPlayer().getCity()))
                //but we are not in that city
                continue;
            Map<Color, List<Disease>> grouped = sc.getValue().getCubes().stream().collect(
                    Collectors.groupingBy(Disease::getColor)
            );
            for (List<Disease> diseases : grouped.values()) {
                int cubes = diseases.size();
                int movingActions = shortestPath.size();
                if (cubes >= 1 && movingActions <= 3) {
                    int treats = 0;
                    int availableMoves = 4 - movingActions;
                    List<ActionType.StandingAction> standingActions = new ArrayList<>();
                    while (treats < t) {
                        var medic = treats > 0 && getCurrentPlayer().getRole() instanceof Medic;

                        if (availableMoves > treats && cubes > treats && !medic)
                            //if medic add only one treat disease action no matter how many cubes
                            standingActions.add(new ActionType.StandingAction(TREAT_DISEASE, sc.getValue(),null,null));
                        treats++;
                    }
                    if (standingActions.size() == t)
                        treatDiseaseMacroActionList.add(new MacroAction.TreatDiseaseMacro(shortestPath, standingActions));
                }
            }

        }
        return treatDiseaseMacroActionList;
    }

    protected static MacroActionFactory getInstance() {
        return instance == null ? (instance = new MacroActionFactory() {
        }) : instance;
    }

    protected static Player getCurrentPlayer() {
        return currentPlayer == null ? state.getPlayerRepository().getCurrentPlayer() : currentPlayer;
    }

    protected static void reset() {
        instance = null;
        actions = null;
        pathFinder = null;
        state = null;
    }

    public static MacroAction findSuitableMacro(int remainingActions) {
        City current = getCurrentPlayer().getCity();
        List<MovingAction> m = new ArrayList<>();
        List<StandingAction> s = new ArrayList<>();
        if (remainingActions == 1) {
            if (current.getCubes().size() >= 1) {
                s.add(new StandingAction(TREAT_DISEASE, current,null,null));
                return MacroAction.macro(m, s);
            }
            City n = current.getNeighbors().get(0);
            m.add(new MovingAction(DRIVE, current, n));
            return MacroAction.macro(m, s);
        }
        if (remainingActions == 2) {
            if (current.getCubes().size() >= 2) {
                s.add(new StandingAction(TREAT_DISEASE, current,null,null));
                s.add(new StandingAction(TREAT_DISEASE, current,null,null));
                return MacroAction.macro(m, s);
            }
            for (City c : current.getNeighbors()) {
                if (c.getCubes().size() >= 1) {
                    m.add(new MovingAction(DRIVE, getCurrentPlayer().getCity(), c));
                    s.add(new StandingAction(TREAT_DISEASE, c,null,null));
                    return MacroAction.macro(m, s);
                }
            }
            City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
            m.add(new MovingAction(DRIVE, current, n));
            m.add(new MovingAction(DRIVE, n, nn));
            return MacroAction.macro(m, s);
        }
        if (remainingActions == 3) {
            if (current.getCubes().size() >= 3) {
                s.add(new StandingAction(TREAT_DISEASE, current,null,null));
                s.add(new StandingAction(TREAT_DISEASE, current,null,null));
                s.add(new StandingAction(TREAT_DISEASE, current,null,null));
                return MacroAction.macro(m, s);
            }
            City save = null;
            for (City c : current.getNeighbors()) {
                if (c.getCubes().size() == 1) save = c;
                if (c.getCubes().size() >= 2) {
                    m.add(new MovingAction(DRIVE, getCurrentPlayer().getCity(), c));
                    s.add(new StandingAction(TREAT_DISEASE, c,null,null));
                    s.add(new StandingAction(TREAT_DISEASE, c,null,null));
                    return MacroAction.macro(m, s);
                }
            }
            if (save != null) {
                m.add(new MovingAction(DRIVE, getCurrentPlayer().getCity(), save));
                s.add(new StandingAction(TREAT_DISEASE, save,null,null));
                s.add(new StandingAction(TREAT_DISEASE, save,null,null));
                return MacroAction.macro(m, s);
            }
            City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
            m.add(new MovingAction(DRIVE, current, n));
            m.add(new MovingAction(DRIVE, n, nn));
            m.add(new MovingAction(DRIVE, nn, nn.getNeighbors().get(0)));
            return MacroAction.macro(m, s);
        }
        throw new IllegalStateException();
    }

    public static MacroAction getNextMacroAction(IState state) {
        init(state);
        MacroAction macro = instance.getActions().get(0);
        instance.getActions().remove(macro);
        return macro;
    }

    protected static MacroActionFactory init(IState newState) {
        reset();
        state = newState;
        pathFinder = new PathFinder.Descriptor(state);
        return getInstance();
    }

    protected static MacroActionFactory init(IState newState, City city, Player player) {
        reset();
        state = newState;
        pathFinder = new PathFinder.Descriptor(state, city, player);
        return getInstance();
    }

    public List<MacroAction> getActions() {
        return actions == null ? (actions = buildAllMacroActions()) : actions;
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
        WalkAway4Macro,
        ShareKnowledgeMacro
    }

}
