package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.pathfinder.PathFinder2;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.rhea.state.StateEvaluation;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.um.nine.headless.agents.rhea.macro.MacroAction.combine;
import static org.um.nine.headless.game.domain.ActionType.*;

public abstract class MacroActionFactory {

    protected static PathFinder2 pathFinder;
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
        actions.addAll(buildGiveKnowledgeMacroActions());
        actions.addAll(buildTakeKnowledgeMacroAction());
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
            case GiveKnowledgeMacro -> buildGiveKnowledgeMacroActions();
            case TakeKnowledgeMacro -> buildTakeKnowledgeMacroAction();
        };
    }

    protected static List<MacroAction> buildWalkAwayMacroActions(int N) {
        List<MacroAction> actions = new ArrayList<>();
        for (City c : state.getCityRepository().getCities().values()) {
            List<MovingAction> shortestPath = pathFinder.shortestPath(c);
            List<StandingAction> s = new ArrayList<>();
            if (shortestPath.size() <= N) {

                for (int i = 0; i < N - shortestPath.size(); i++)
                    s.add(new ActionType.StandingAction(NO_ACTION, c, null, null));

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


                if (shortestPath.size() > 0 && shortestPath.size() < 4 && !pathFinder.isSpendingCard(shortestPath, cc.getCity())) {
                    List<StandingAction> s = new ArrayList<>();
                    s.add(new ActionType.StandingAction(BUILD_RESEARCH_STATION, cc.getCity(), null, null));
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
                    if (shortestPath.size() <= 3 && shortestPath.size() > 0 && !pathFinder.isSpendingCard(shortestPath, c))
                        curingActions.add(MacroAction.macro(
                                shortestPath, List.of(new ActionType.StandingAction(DISCOVER_CURE, c, null, null))
                        ));
                }
            }
        }
        return curingActions;
    }

    protected static List<MacroAction> buildGiveKnowledgeMacroActions() {
        List<MacroAction> shareKnowledgeActions = new ArrayList<>();
        List<PlayerCard> cardsInHand = currentPlayer.getHand();
        List<Player> otherPlayers = new ArrayList<>();
        for (Map.Entry<String, Player> entry : state.getPlayerRepository().getPlayers().entrySet()) {
            if (entry.getValue() != currentPlayer) otherPlayers.add(entry.getValue());
        }

        //TODO: Make sure this is how you check Role
        if (currentPlayer.getRole().getName().equals("Researcher")) {
            //Researcher can give any city card
            List<City> citiesInImmediateRange = new ArrayList<>();
            List<City> citiesInRange = new ArrayList<>();
            for (Map.Entry<String, City> c : state.getCityRepository().getCities().entrySet()) {
                int distance = pathFinder.shortestPath(c.getValue()).size();
                if (distance < 4) citiesInImmediateRange.add(c.getValue());
                else if (distance == 4) citiesInRange.add(c.getValue());
            }

            List<City> citiesNotCoveredYet = new ArrayList<>();
            for (Player p : otherPlayers) {
                for (City c : citiesInImmediateRange) {
                    if (p.getCity() == c) {
                        for (PlayerCard card : cardsInHand) {
                            if (card instanceof CityCard) {
                                City cardCity = ((CityCard) card).getCity();
                                shareKnowledgeActions.add(MacroAction.macro(pathFinder.shortestPath(c),
                                        List.of(new ActionType.StandingAction(SHARE_KNOWLEDGE, cardCity, currentPlayer, p))));
                            }
                        }
                    } else citiesNotCoveredYet.add(c);
                }
            }

            for (City c : citiesNotCoveredYet) {
                List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
                shareKnowledgeActions.add(MacroAction.macro(shortestPath, null));
            }
            for (City c : citiesInRange) {
                List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
                shareKnowledgeActions.add(MacroAction.macro(shortestPath, null));
            }

        } else {

            List<City> citiesInHandInImmediateReach = new ArrayList<>();
            List<City> citiesInHandInReach = new ArrayList<>();
            for (PlayerCard card : cardsInHand) {//TODO: Make sure shortestPath check is correct
                if (card instanceof CityCard && pathFinder.shortestPath(((CityCard) card).getCity()).size() < 4)
                    citiesInHandInImmediateReach.add(((CityCard) card).getCity());
                else if (card instanceof CityCard && pathFinder.shortestPath(((CityCard) card).getCity()).size() == 4)
                    citiesInHandInReach.add(((CityCard) card).getCity());
            }

            List<Player> playersInCities = new ArrayList<>();
            for (Player p : otherPlayers) {
                for (City c : citiesInHandInImmediateReach) {
                    if (c == p.getCity()) playersInCities.add(p);
                }
            }

        //TODO: check if pathFinder.isSpendingCard(path,card.city) first, to ensure keeping that card through the path
        //TODO: return actions
        //playersInCities contains Players at cities we could go to and give a card immediately
        //citiesInHand contains cities that currentPlayer can reach in one turn (includes the cities where there are other players)
            for (Player p : playersInCities) {
                City c = p.getCity();
                citiesInHandInImmediateReach.remove(c);
                //this city can be removed from the other list, seeing as it is handled here
                shareKnowledgeActions.add(MacroAction.macro(pathFinder.shortestPath(c), List.of(new ActionType.StandingAction(SHARE_KNOWLEDGE, c, currentPlayer, p))));
            }

            for (City c : citiesInHandInImmediateReach) {
                List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
                shareKnowledgeActions.add(MacroAction.macro(shortestPath, null));
            }
            for (City c : citiesInHandInReach) {
                List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
                shareKnowledgeActions.add(MacroAction.macro(shortestPath, null));
            }

        }
        return shareKnowledgeActions;
    }

    protected static List<MacroAction> buildTakeKnowledgeMacroAction() {
        List<MacroAction> shareKnowledgeActions = new ArrayList<>();
        List<City> citiesInImmediateRange = new ArrayList<>();
        List<City> citiesInRange = new ArrayList<>();
        List<Player> playersInReachableCities = new ArrayList<>();
        Player researcher = null;

        List<Player> otherPlayers = new ArrayList<>();
        for (Map.Entry<String, Player> entry : state.getPlayerRepository().getPlayers().entrySet()) {
            if (entry.getValue() != currentPlayer) otherPlayers.add(entry.getValue());
        }

        for (Player p : otherPlayers) {
            List<PlayerCard> cardsInHand = p.getHand();
            if (p.getRole().getName().equals("Researcher")) {//check if the researcher is in an immediately reachable city
                if (pathFinder.shortestPath(p.getCity()).size() < 4) researcher = p;
            } else {
                for (PlayerCard card : cardsInHand) {//TODO: Make sure shortestPath check is correct
                    if (card instanceof CityCard && pathFinder.shortestPath(((CityCard) card).getCity()).size() < 4) {
                        City c = ((CityCard) card).getCity();
                        citiesInImmediateRange.add(c);
                        if (c == p.getCity()) playersInReachableCities.add(p);
                    } else if (card instanceof CityCard && pathFinder.shortestPath(((CityCard) card).getCity()).size() == 4) {
                        citiesInRange.add(((CityCard) card).getCity());
                    }
                }
            }
        }

        if(researcher!=null){
            for (PlayerCard pc:researcher.getHand()) {
                if(pc instanceof CityCard){
                    City c = ((CityCard) pc).getCity();
                    shareKnowledgeActions.add(MacroAction.macro(pathFinder.shortestPath(c), List.of(new ActionType.StandingAction(SHARE_KNOWLEDGE, c, researcher, currentPlayer))));
                }
            }
        }

        for (Player p : playersInReachableCities) {
            City c = p.getCity();
            citiesInImmediateRange.remove(c); //this city can be removed from the other list, seeing as it is handled here
            shareKnowledgeActions.add(MacroAction.macro(pathFinder.shortestPath(c), List.of(new ActionType.StandingAction(SHARE_KNOWLEDGE, c, p, currentPlayer))));
        }

        for (City c : citiesInImmediateRange) {
            List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
            shareKnowledgeActions.add(MacroAction.macro(shortestPath, null));
        }
        for (City c : citiesInRange) {
            List<ActionType.MovingAction> shortestPath = pathFinder.shortestPath(c);
            shareKnowledgeActions.add(MacroAction.macro(shortestPath, null));
        }

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
                            standingActions.add(new ActionType.StandingAction(TREAT_DISEASE, sc.getValue(), null, null));
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

    public static MacroAction findSuitableMacro(MacroAction toFill) {
        int remainingActions = 4 - toFill.size();
        City current;
        if (toFill.getAtIndex(toFill.size() - 1) instanceof MovingAction m) {
            current = m.toCity();
        } else if (toFill.getAtIndex(toFill.size() - 1) instanceof StandingAction s) {
            current = s.applyTo();
        } else throw new IllegalStateException();

        int alreadyCured = ((int) toFill.standingActions().stream().filter(s -> s.action().equals(TREAT_DISEASE) && s.applyTo().equals(current)).count());
        int cubesInCity = current.getCubes().size();
        int diseasesLeft = cubesInCity - alreadyCured;
        List<MovingAction> m = new ArrayList<>();
        List<StandingAction> s = new ArrayList<>();
        switch (remainingActions) {
            case 1 -> {
                if (diseasesLeft >= 1) {
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, MacroAction.macro(m, s));
                }
                City n = current.getNeighbors().get(0);
                m.add(new MovingAction(DRIVE, current, n));
                return combine(toFill, MacroAction.macro(m, s));
            }
            case 2 -> {
                if (diseasesLeft >= 2) {
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, MacroAction.macro(m, s));
                }
                for (City c : current.getNeighbors()) {
                    if (diseasesLeft >= 1 && c.getCubes().size() >= 1) {
                        m.add(new MovingAction(DRIVE, getCurrentPlayer().getCity(), c));
                        s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                        return combine(toFill, MacroAction.macro(m, s));
                    }
                }
                City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
                m.add(new MovingAction(DRIVE, current, n));
                m.add(new MovingAction(DRIVE, n, nn));
                return combine(toFill, MacroAction.macro(m, s));
            }
            case 3 -> {
                if (diseasesLeft >= 3) {
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, MacroAction.macro(m, s));
                }
                City save = null;
                for (City c : current.getNeighbors()) {
                    if (c.getCubes().size() == 1) save = c;
                    if (c.getCubes().size() >= 2) {
                        m.add(new MovingAction(DRIVE, getCurrentPlayer().getCity(), c));
                        s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                        s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                        return combine(toFill, MacroAction.macro(m, s));
                    }
                }
                if (save != null) {
                    m.add(new MovingAction(DRIVE, getCurrentPlayer().getCity(), save));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, MacroAction.macro(m, s));
                }
                City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
                m.add(new MovingAction(DRIVE, current, n));
                m.add(new MovingAction(DRIVE, n, nn));
                m.add(new MovingAction(DRIVE, nn, nn.getNeighbors().get(0)));
                return combine(toFill, MacroAction.macro(m, s));
            }
            default -> {
                return toFill;
            }
        }

    }

    public static MacroAction getNextMacroAction(IState state, Player player) {
        init(state, player.getCity(), player);
        MacroAction macro = instance.getActions().get(0);
        instance.getActions().remove(macro);
        return macro;
    }

    protected static MacroActionFactory init(IState newState, City city, Player player) {
        reset();
        state = newState;
        pathFinder = new PathFinder2(state, city, player);
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
        GiveKnowledgeMacro,
        TakeKnowledgeMacro
    }

}
