package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.pathfinder.PathFinder2;
import org.um.nine.headless.agents.rhea.pathfinder.PathFinder3;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.rhea.state.StateEvaluation;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.Researcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.um.nine.headless.agents.rhea.macro.MacroAction.combine;
import static org.um.nine.headless.agents.rhea.macro.MacroAction.macro;
import static org.um.nine.headless.game.domain.ActionType.*;

public abstract class MacroActionFactory {

    protected PathFinder3 pathFinder;
    protected IState state;
    protected List<MacroAction> actions;
    protected Player currentPlayer;
    protected static MacroActionFactory instance;

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
        actions.addAll(buildDiscoverCureMacroActions());
        actions.addAll(buildGiveKnowledgeMacroActions(4));
        actions.addAll(buildTakeKnowledgeMacroAction(4));
        actions.addAll(buildResearchStationMacroActions());
        actions.addAll(buildWalkAwayMacroActions(4));
        return actions;
    }

    protected static List<MacroAction> buildMacroActions(MacroType type) {
        return switch (type) {
            case Treat1Macro -> buildTreatDiseaseMacroActions(1);
            case Treat2Macro -> buildTreatDiseaseMacroActions(2);
            case Treat3Macro -> buildTreatDiseaseMacroActions(3);
            case CureMacro -> buildDiscoverCureMacroActions();
            case GiveKnowledgeMacro -> buildGiveKnowledgeMacroActions(4);
            case TakeKnowledgeMacro -> buildTakeKnowledgeMacroAction(4);
            case ResearchStationMacro -> buildResearchStationMacroActions();
            case WalkAway1Macro -> buildWalkAwayMacroActions(1);
            case WalkAway2Macro -> buildWalkAwayMacroActions(2);
            case WalkAway3Macro -> buildWalkAwayMacroActions(3);
            case WalkAway4Macro -> buildWalkAwayMacroActions(4);
        };
    }

    protected static List<MacroAction> buildWalkAwayMacroActions(int N) {
        List<MacroAction> actions = new ArrayList<>();
        for (City c : getInstance().state.getCityRepository().getCities().values()) {
            List<MovingAction> shortestPath = getInstance().pathFinder.lightestPath(c);
            List<StandingAction> s = new ArrayList<>();
            if (shortestPath.size() <= N) {
                for (int i = 0; i < N - shortestPath.size(); i++)
                    s.add(new ActionType.StandingAction(NO_ACTION, c, null, null));
                actions.add(macro(shortestPath, s));
            }
        }
        return actions.stream().sorted(Comparator.comparingInt(ma -> ((MacroAction) ma).movingActions().size()).reversed()).collect(Collectors.toList());
    }

    protected static List<MacroAction> buildResearchStationMacroActions() {
        List<MacroAction> actions = new ArrayList<>();
        if (getInstance().state.getCityRepository().getResearchStations().size() >= Settings.RESEARCH_STATION_THRESHOLD)
            return actions;

        for (PlayerCard pc : getInstance().getCurrentPlayer().getHand()) {

            if (pc instanceof CityCard cc) {
                if (cc.getCity().getResearchStation() != null) continue;


                //check if all research stations are at least distance 3 from this new RS
                //need a new pathfinder from this city

                PathFinder2 pf = PathFinder2.evaluateWalkingPath(getInstance().state, cc.getCity());

                var reachableWalkingMinDistance = getInstance().state.
                        getCityRepository().
                        getResearchStations().
                        stream().
                        map(ResearchStation::getCity).
                        filter(city -> pf.lightestPath(city).size() > 3).findFirst().orElse(null);


                if (reachableWalkingMinDistance != null) {
                    List<ActionType.MovingAction> shortestPath = getInstance().pathFinder.getPath(cc.getCity(), true);
                    if (shortestPath.size() > 0 && shortestPath.size() < 4) {
                        List<StandingAction> s = new ArrayList<>();
                        s.add(new ActionType.StandingAction(BUILD_RESEARCH_STATION, cc.getCity(), null, null));
                        actions.add(macro(shortestPath, s));
                    }
                }
            }
        }
        return actions;
    }

    protected static List<MacroAction> buildDiscoverCureMacroActions() {
        List<MacroAction> curingActions = new ArrayList<>();

        Player current = getInstance().getCurrentPlayer();
        int needed = StateEvaluation.Cd(current);

        for (Cure cure : getInstance().state.getDiseaseRepository().getCures().values()) {
            if (cure.isDiscovered()) continue;
            Color color = cure.getColor();
            long inHand = current.getHand().stream().
                    filter(pc ->
                            pc instanceof CityCard cc && cc.getCity().getColor().equals(color)
                    ).count();
            if (inHand >= needed) {
                if (current.getCity().getResearchStation() != null) {
                    curingActions.add(macro(new ArrayList<>(), new ArrayList<>(
                            List.of(new StandingAction(
                                    DISCOVER_CURE,
                                    current.getCity(),
                                    null,
                                    null)))
                    ));
                    return curingActions;
                }
                List<City> stations = getInstance().state.getCityRepository().
                        getResearchStations().
                        stream().
                        map(ResearchStation::getCity).
                        collect(Collectors.toList());

                City closestResearchStation = stations.stream().
                        filter(city -> getInstance().pathFinder.getPath(city, true).size() > 0).
                        min(Comparator.comparingInt(city -> getInstance().pathFinder.getPath(city, true).size())).
                        orElse(null);


                if (closestResearchStation != null) {

                    List<ActionType.MovingAction> shortestPath = getInstance().pathFinder.getPath(closestResearchStation, true);

                    if (shortestPath.size() == 4) {
                        MacroAction curingLater = macro(shortestPath, new ArrayList<>());
                        curingActions.add(curingLater);
                    } else {
                        curingActions.add(macro(shortestPath, new ArrayList<>(List.of(new ActionType.StandingAction(DISCOVER_CURE, closestResearchStation, null, null)))));
                    }
                }


            }
        }
        return curingActions;
    }

    protected static List<MacroAction> buildShareKnowledgeMacroActions(int N){
        List<MacroAction> allMacros = buildGiveKnowledgeMacroActions(N);
        allMacros.addAll(buildTakeKnowledgeMacroAction(N));
        return allMacros;
    }

    /**
     *
     * @param N actions remaining when starting Macro
     * @return
     */
    protected static List<MacroAction> buildGiveKnowledgeMacroActions(int N) {
        List<MacroAction> shareKnowledgeActions = new ArrayList<>();
        Player currentPlayer = getInstance().getCurrentPlayer();
        List<PlayerCard> cardsInHand = currentPlayer.getHand();
        List<Player> otherPlayers = new ArrayList<>();
        for (Map.Entry<String, Player> entry : getInstance().state.getPlayerRepository().getPlayers().entrySet()) {
            if (entry.getValue() != getInstance().currentPlayer) otherPlayers.add(entry.getValue());
        }

        if (getInstance().currentPlayer.getRole() instanceof Researcher) {
            //Researcher can give any city card
            List<City> citiesInImmediateRange = new ArrayList<>();
            List<City> citiesInRange = new ArrayList<>();
            for (Map.Entry<String, City> c : getInstance().state.getCityRepository().getCities().entrySet()) {
                int distance = getInstance().pathFinder.getPath(c.getValue(), true).size();
                if (distance < N) citiesInImmediateRange.add(c.getValue());
                else if (distance == N) citiesInRange.add(c.getValue());
            }

            List<City> citiesNotCoveredYet = new ArrayList<>();
            for (Player p : otherPlayers) {
                for (City c : citiesInImmediateRange) {
                    if (p.getCity() == c) {
                        for (PlayerCard card : cardsInHand) {
                            if (card instanceof CityCard) {
                                City cardCity = ((CityCard) card).getCity();
                                addShareAndWait(N, shareKnowledgeActions, getInstance().currentPlayer, p, cardCity);
                            }
                        }
                    } else citiesNotCoveredYet.add(c);
                }
            }
            addWait(N,shareKnowledgeActions,citiesNotCoveredYet);
            addWait(N,shareKnowledgeActions,citiesInRange);

        } else {

            List<City> citiesInHandInImmediateReach = new ArrayList<>();
            List<City> citiesInHandInReach = new ArrayList<>();

            for (PlayerCard card : cardsInHand) {
                if (card instanceof CityCard cc && getInstance().pathFinder.getPath(cc.getCity(), true).size() < N)
                    citiesInHandInImmediateReach.add(cc.getCity());
                else if (card instanceof CityCard cc && getInstance().pathFinder.getPath(cc.getCity(), true).size() == N)
                    citiesInHandInReach.add(cc.getCity());
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
                addShareAndWait(N, shareKnowledgeActions, getInstance().currentPlayer, p, c);
            }

            addWait(N, shareKnowledgeActions, citiesInHandInImmediateReach);
            addWait(N, shareKnowledgeActions, citiesInHandInReach);

        }
        return shareKnowledgeActions;
    }
    /**
     *
     * @param N actions remaining when starting Macro
     * @return
     */
    protected static List<MacroAction> buildTakeKnowledgeMacroAction(int N) {
        List<MacroAction> shareKnowledgeActions = new ArrayList<>();
        List<City> citiesInImmediateRange = new ArrayList<>();
        List<City> citiesInRange = new ArrayList<>();
        List<Player> playersInReachableCities = new ArrayList<>();
        Player researcher = null;

        List<Player> otherPlayers = new ArrayList<>();
        for (Map.Entry<String, Player> entry : getInstance().state.getPlayerRepository().getPlayers().entrySet()) {
            if (entry.getValue() != getInstance().currentPlayer) otherPlayers.add(entry.getValue());
        }

        for (Player p : otherPlayers) {
            List<PlayerCard> cardsInHand = p.getHand();
            if (p.getRole() instanceof Researcher) {
                //check if the researcher is in an immediately reachable city
                if (getInstance().pathFinder.getPath(p.getCity(), true).size() < N) researcher = p;
            } else {
                for (PlayerCard card : cardsInHand) {
                    if (card instanceof CityCard cc && getInstance().pathFinder.getPath(cc.getCity(), true).size() < N) {
                        City c = cc.getCity();
                        citiesInImmediateRange.add(c);
                        if (c == p.getCity()) playersInReachableCities.add(p);
                    } else if (card instanceof CityCard cc && getInstance().pathFinder.getPath(cc.getCity(), true).size() == N) {
                        citiesInRange.add(cc.getCity());
                    }
                }
            }
        }
        if(researcher!=null){
            for (PlayerCard pc:researcher.getHand()) {
                if (pc instanceof CityCard cc) {
                    City c = cc.getCity();
                    addShareAndWait(N, shareKnowledgeActions, researcher, getInstance().currentPlayer, c);

                }
            }
        }
        for (Player p : playersInReachableCities) {
            City c = p.getCity();
            citiesInImmediateRange.remove(c); //this city can be removed from the other list, seeing as it is handled here
            addShareAndWait(N, shareKnowledgeActions, p, getInstance().currentPlayer, c);
        }
        addWait(N, shareKnowledgeActions, citiesInImmediateRange);
        addWait(N, shareKnowledgeActions, citiesInRange);

        return shareKnowledgeActions;
    }
    private static void addShareAndWait(int N, List<MacroAction> shareKnowledgeActions, Player give, Player take, City c) {
        List<MovingAction> shortestPath = getInstance().pathFinder.getPath(c, true);
        List<StandingAction> standing = new ArrayList<>();
        standing.add(new StandingAction(SHARE_KNOWLEDGE, c, give, take));
        for (int i = 0; i < N-shortestPath.size()-1; i++) {
            standing.add(new StandingAction(SKIP_ACTION,c,null,null));
        }
        shareKnowledgeActions.add(macro(shortestPath, standing));
    }
    private static void addWait(int N, List<MacroAction> shareKnowledgeActions, List<City> citiesInRange) {
        for (City c : citiesInRange) {
            List<MovingAction> shortestPath = getInstance().pathFinder.getPath(c, true);
            List<StandingAction> skip = new ArrayList<>();
            for (int i = 0; i < N-shortestPath.size(); i++) {
                skip.add(new StandingAction(SKIP_ACTION,c,null,null));
            }
            shareKnowledgeActions.add(macro(shortestPath, skip));
        }
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
        for (Map.Entry<String, City> sc : getInstance().state.getCityRepository().getCities().entrySet()) {
            List<ActionType.MovingAction> shortestPath =
                    t == 3 ? getInstance().pathFinder.getPath(sc.getValue(), false) :
                            getInstance().pathFinder.lightestPath(sc.getValue());
            if (shortestPath.size() == 0 && !sc.getValue().equals(getInstance().getCurrentPlayer().getCity()))
                continue;
            Map<Color, List<Disease>> grouped = sc.getValue().getCubes().stream().collect(
                    Collectors.groupingBy(Disease::getColor)
            );

            for (Map.Entry<Color, List<Disease>> diseases : grouped.entrySet()) {
                int cubes = diseases.getValue().size();
                int movingActions = shortestPath.size();
                if (cubes >= 1 && movingActions <= 3) {
                    int treats = 0;
                    int availableMoves = 4 - movingActions;
                    List<ActionType.StandingAction> standingActions = new ArrayList<>();
                    boolean medic = getInstance().getCurrentPlayer().getRole() instanceof Medic
                            || getInstance().state.getDiseaseRepository().getCures().get(diseases.getKey()).isDiscovered();
                    while (treats < t) {
                        boolean m = treats >= 1 && medic;
                        if (availableMoves > treats && cubes > treats && !m)
                            //if medic or cure discovered add only one treat disease action no matter how many cubes
                            standingActions.add(new ActionType.StandingAction(TREAT_DISEASE, sc.getValue(), null, null));
                        treats++;
                    }
                    if (standingActions.size() == t || (medic && cubes == t))
                        treatDiseaseMacroActionList.add(new MacroAction.TreatDiseaseMacro(shortestPath, standingActions));
                }
            }

        }
        return treatDiseaseMacroActionList;
    }


    protected Player getCurrentPlayer() {
        return getInstance().currentPlayer == null ? getInstance().state.getPlayerRepository().getCurrentPlayer() : getInstance().currentPlayer;
    }

    public static MacroAction fillMacroAction(MacroAction toFill) {
        int remainingActions = 4 - toFill.size();
        if (remainingActions < 1) return toFill;

        List<MovingAction> m;
        List<StandingAction> s = new ArrayList<>();


        if (toFill.getAtIndex(0) instanceof MovingAction ma) {

            if (ma.fromCity().getCubes().size() > 0) {
                if (getInstance().getCurrentPlayer().getRole() instanceof Medic) {
                    s.add(new StandingAction(TREAT_DISEASE, ma.fromCity(), null, null));
                } else {
                    for (int i = 0, j = ma.fromCity().getCubes().size(); i < remainingActions && j > 0; i++, j--) {
                        s.add(new StandingAction(TREAT_DISEASE, ma.fromCity(), null, null));
                    }
                }

                toFill = combine(macro(new ArrayList<>(), s), toFill);
            }
        }

        remainingActions = 4 - toFill.size();
        if (remainingActions < 1) return toFill;

        m = new ArrayList<>();
        s = new ArrayList<>();
        City current;
        if (toFill.getAtIndex(toFill.size() - 1) instanceof MovingAction ma) {
            current = ma.toCity();
        } else if (toFill.getAtIndex(toFill.size() - 1) instanceof StandingAction sa) {
            current = sa.applyTo();
        } else throw new IllegalStateException();

        int alreadyCured = ((int) toFill.standingActions().stream().filter(sta -> sta.action().equals(TREAT_DISEASE) && sta.applyTo().equals(current)).count());
        int cubesInCity = current.getCubes().size();
        int diseasesLeft = cubesInCity - alreadyCured;
        if (getInstance().getCurrentPlayer().getRole() instanceof Medic) {
            if (alreadyCured > 0) diseasesLeft = 0;
            else diseasesLeft = 1;
        }

        switch (remainingActions) {
            case 1 -> {
                if (diseasesLeft >= 1) {
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, macro(m, s));
                }
                City n = current.getNeighbors().get(0);
                m.add(new MovingAction(DRIVE, current, n));
                return combine(toFill, macro(m, s));
            }
            case 2 -> {
                if (diseasesLeft >= 2) {
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, macro(m, s));
                }
                for (City c : current.getNeighbors()) {
                    if (diseasesLeft >= 1 && c.getCubes().size() >= 1) {
                        m.add(new MovingAction(DRIVE, current, c));
                        s.add(new StandingAction(TREAT_DISEASE, c, null, null));
                        return combine(toFill, macro(m, s));
                    }
                }
                City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
                m.add(new MovingAction(DRIVE, current, n));
                m.add(new MovingAction(DRIVE, n, nn));
                return combine(toFill, macro(m, s));
            }
            case 3 -> {
                if (diseasesLeft >= 3) {
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, macro(m, s));
                }
                City save = null;
                for (City c : current.getNeighbors()) {
                    if (c.getCubes().size() == 1) save = c;
                    if (c.getCubes().size() >= 2) {
                        m.add(new MovingAction(DRIVE, getInstance().getCurrentPlayer().getCity(), c));
                        s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                        s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                        return combine(toFill, macro(m, s));
                    }
                }
                if (save != null) {
                    m.add(new MovingAction(DRIVE, getInstance().getCurrentPlayer().getCity(), save));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    s.add(new StandingAction(TREAT_DISEASE, current, null, null));
                    return combine(toFill, macro(m, s));
                }
                City n = current.getNeighbors().get(0), nn = n.getNeighbors().get(0);
                m.add(new MovingAction(DRIVE, current, n));
                m.add(new MovingAction(DRIVE, n, nn));
                m.add(new MovingAction(DRIVE, nn, nn.getNeighbors().get(0)));
                return combine(toFill, macro(m, s));
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

    protected static MacroActionFactory init(IState state, City city, Player player) {
        return getInstance().initialise(state, city, player);
    }

    public List<MacroAction> getActions() {
        return getInstance().actions == null ? (getInstance().actions = buildAllMacroActions()) : getInstance().actions;
    }

    protected abstract MacroAction getNextMacroAction();

    protected abstract MacroActionFactory initialise(IState state, City city, Player player);

    protected static MacroActionFactory getInstance() {
        // something's wrong if you get here
        return instance == null ? (instance = new MacroActionFactory() {
            @Override
            protected MacroAction getNextMacroAction() {
                throw new IllegalStateException();
            }

            @Override
            protected MacroActionFactory initialise(IState state, City city, Player player) {
                throw new IllegalStateException();
            }
        }) : instance;
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
