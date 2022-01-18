package org.um.nine.headless.agents.rhea.macro;

import org.um.nine.headless.agents.rhea.pathfinder.PathFinder3;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.rhea.state.StateEvaluation;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.roles.Medic;
import org.um.nine.headless.game.domain.roles.Researcher;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.um.nine.headless.agents.rhea.macro.MacroAction.*;
import static org.um.nine.headless.game.Settings.RESEARCH_STATION_THRESHOLD;

public abstract class MacroActionFactory2 implements IReportable {
    protected PathFinder3 pathFinder;
    protected IState state;
    protected Player currentPlayer;
    protected static MacroActionFactory2 instance;
    protected City currentCity;
    protected List<MacroAction> actions;

    protected MacroActionFactory2() {
    }

    protected MacroActionFactory2 initialise(IState state, City city, Player player) {
        this.actions = null;
        this.state = state;
        this.currentPlayer = player;
        this.pathFinder = new PathFinder3(state, player, city);
        this.pathFinder.evaluatePaths();
        return this;
    }

    protected abstract MacroAction getNextMacroAction();

    protected List<MacroAction> buildAllMacroActions() {
        List<MacroAction> actions = new ArrayList<>();
        actions.addAll(buildDiscoverCureMacroActions());
        actions.addAll(buildTreatDiseaseMacroActions(3));
        actions.addAll(buildTakeKnowledgeMacroActions());
        actions.addAll(buildGiveKnowledgeMacroActions());
        actions.addAll(buildBuildRSMacroActions());
        actions.addAll(buildTreatDiseaseMacroActions(2));
        actions.addAll(buildTreatDiseaseMacroActions(1));
        actions.addAll(buildWalkAwayMacroActions(4));
        actions.addAll(buildWalkAwayMacroActions(3));
        actions.addAll(buildWalkAwayMacroActions(2));
        actions.addAll(buildWalkAwayMacroActions(1));
        return actions;
    }

    protected List<MacroAction> buildDiscoverCureMacroActions() {
        List<MacroAction> curingActions = new ArrayList<>();
        Player current = getCurrentPlayer();
        int needed = StateEvaluation.Cd(current);
        Color cureColor = current.getHand().
                stream().
                map(pc -> (CityCard) pc).
                map(CityCard::getCity).
                collect(Collectors.groupingBy(City::getColor)).
                values().
                stream().
                filter(list -> list.size() >= needed).
                map(list -> list.get(0).getColor()).
                findFirst().
                orElse(null);

        Cure toDiscover = cureColor == null ? null : state.getDiseaseRepository().getCures().get(cureColor);

        if (toDiscover == null || toDiscover.isDiscovered()) return curingActions;

        City closestRS;
        List<ActionType.MovingAction> pathToClosestRs;
        boolean cureLater = false;
        if (getCurrentCity().getResearchStation() != null) {
            closestRS = getCurrentCity();
            pathToClosestRs = new ArrayList<>();
        } else {
            pathToClosestRs = state.
                    getCityRepository().
                    getResearchStations().
                    stream().
                    map(ResearchStation::getCity).
                    map(city -> {
                        List<ActionType.MovingAction> path = pathFinder.getPath(city, false);

                        for (CityCard cc : getCurrentPlayer().
                                getHand().stream().
                                map(pc -> (CityCard) pc).
                                filter(cc -> cc.getCity().getColor().equals(cureColor)).
                                collect(Collectors.toList()))
                            if (pathFinder.isSpendingCard(path, cc.getCity()))
                                path = pathFinder.lightestPath(city);
                        return path;
                    }).
                    filter(path -> path.size() > 0).
                    min(Comparator.comparingInt(List::size)).
                    orElse(null);

            closestRS = pathToClosestRs == null ? null : pathToClosestRs.get(pathToClosestRs.size() - 1).toCity();
            cureLater = pathToClosestRs != null && pathToClosestRs.size() == 4;
        }
        if (cureLater) {
            curingActions.add(macro(pathToClosestRs, new ArrayList<>()));
            return curingActions;
        }
        if (closestRS != null) {
            List<ActionType.StandingAction> cure = new ArrayList<>();
            cure.add(new ActionType.StandingAction(ActionType.DISCOVER_CURE, closestRS));
            curingActions.add(macro(pathToClosestRs, cure));
        }
        return curingActions;
    }

    protected List<MacroAction> buildTreatDiseaseMacroActions(int diseasesToTreat) {
        List<MacroAction> treatingActions = new ArrayList<>();

        //get all the reachable cities with at least a cube,
        //spend 3 moving actions at most (leave 1 action at least)
        List<City> reachableCities = state.
                getCityRepository().
                getCities().
                values().
                stream().
                filter(city -> {
                    int pathSize = pathFinder.getPath(city, false).size();
                    return city.equals(getCurrentCity()) || (pathSize > 0 && pathSize < 4);
                }).
                filter(city -> city.getCubes().size() > 0).
                collect(Collectors.toList());

        boolean medic = getCurrentPlayer().getRole() instanceof Medic;

        if (medic) return buildTreatDiseaseMacroActionsMedic(diseasesToTreat, reachableCities);

        for (City reachable : reachableCities) {
            List<ActionType.MovingAction> path = pathFinder.getPath(reachable, false);  //get any shortest path
            int actionsLeft = 4 - path.size();  // actions we need to fill

            boolean discovered = false;
            int discoveredTreatable = 0;
            int notDiscoveredTreatable = 0;
            int treatingActionsCount = 0;

            Map<Color, List<Disease>> groupedDiseases = reachable.getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));

            for (Map.Entry<Color, List<Disease>> diseaseEntry : groupedDiseases.entrySet()) {
                if (state.getDiseaseRepository().getCures().get(diseaseEntry.getKey()).isDiscovered()) {
                    if (diseaseEntry.getValue().size() > 0) {
                        discovered = true;
                        discoveredTreatable++;
                    }
                } else notDiscoveredTreatable += diseaseEntry.getValue().size();
            }

            if (discovered && actionsLeft < 1 || (!discovered && actionsLeft < diseasesToTreat))
                continue;  // we cannot fill the macro with n treat disease actions
            if (!discovered && reachable.getCubes().size() < diseasesToTreat)
                continue;                     // we need enough cubes to fill the macro
            if (discovered && (discoveredTreatable + notDiscoveredTreatable < diseasesToTreat))
                continue;   // we can treat discovered (1 action all cubes) and not discovered (1 action each cube)

            List<ActionType.StandingAction> treatingCity = new ArrayList<>();

            if (discovered) {
                for (int i = 0; i < diseasesToTreat && i < discoveredTreatable; i++) {
                    treatingCity.add(new ActionType.StandingAction(ActionType.TREAT_DISEASE, reachable));  //add the discovered
                    discoveredTreatable--;
                    treatingActionsCount++;
                }
                for (int i = actionsLeft - discoveredTreatable; i < diseasesToTreat && i < notDiscoveredTreatable; i++) {
                    treatingCity.add(new ActionType.StandingAction(ActionType.TREAT_DISEASE, reachable));  //add the not discovered
                    notDiscoveredTreatable--;
                    treatingActionsCount++;
                }

            } else {
                for (int i = 0; i < diseasesToTreat; i++) {
                    treatingCity.add(new ActionType.StandingAction(ActionType.TREAT_DISEASE, reachable));   // just add treating actions until diseases to treat
                    notDiscoveredTreatable--;
                    treatingActionsCount++;
                }
            }

            actionsLeft -= treatingActionsCount;
            // time to debug

            if (discoveredTreatable < 0 || notDiscoveredTreatable < 0 || treatingActionsCount > diseasesToTreat || actionsLeft < 0)
                throw new IllegalStateException("Something wrong when building treat disease macro actions");

            treatingActions.add(macro(path, treatingCity));
        }


        Predicate<ActionType.MovingAction> expensive = ma -> ma.action().equals(ActionType.DIRECT_FLIGHT) || ma.action().equals(ActionType.CHARTER_FLIGHT);
        Comparator<List<ActionType.MovingAction>> comparator = Comparator.comparingLong(list -> list.stream().filter(expensive).count());
        return treatingActions.stream().sorted((m1, m2) -> comparator.compare(m1.movingActions(), m2.movingActions())).collect(Collectors.toList());
    }

    protected List<MacroAction> buildTreatDiseaseMacroActionsMedic(int diseasesToTreat, List<City> reachableCities) {
        List<MacroAction> treatingActions = new ArrayList<>();
        for (City reachable : reachableCities) {
            List<ActionType.MovingAction> path = pathFinder.getPath(reachable, false);  //get any shortest path
            int actionsLeft = 4 - path.size();                                                            // actions we need to fill
            if (actionsLeft == 0) continue;
            int treatingActionsCount = 0;

            Map<Color, List<Disease>> groupedDiseases = reachable.getCubes().stream().collect(Collectors.groupingBy(Disease::getColor));
            if (groupedDiseases.entrySet().size() < diseasesToTreat) continue;
            // we know the medic cures all diseases by color with one action
            // we also know the medic will automatically remove the cubes of the discovered cure color
            int nTreatableDisease = (int) groupedDiseases.keySet().stream().map(color -> state.getDiseaseRepository().getCures().get(color)).filter(cure -> !cure.isDiscovered()).count();
            if (nTreatableDisease < diseasesToTreat) continue;

            List<ActionType.StandingAction> treatingCity = new ArrayList<>();

            for (int i = 0; i < diseasesToTreat; i++) {
                treatingCity.add(new ActionType.StandingAction(ActionType.TREAT_DISEASE, reachable));
                nTreatableDisease--;
                treatingActionsCount++;
                actionsLeft--;
            }

            if (nTreatableDisease < 0)
                throw new IllegalStateException("Wrong treatable disease count when building Medic role treat disease macro actions");
            if (actionsLeft < 0)
                throw new IllegalStateException("Wrong actions left count when building Medic role treat disease macro actions");
            if (treatingActionsCount != diseasesToTreat)
                throw new IllegalStateException("Something wrong when building Medic role treat disease macro actions");

            treatingActions.add(macro(path, treatingCity));
        }

        return treatingActions;
    }

    protected List<MacroAction> buildBuildRSMacroActions() {
        List<MacroAction> buildingActions = new ArrayList<>();
        if (state.getCityRepository().getResearchStations().size() >= RESEARCH_STATION_THRESHOLD)
            return buildingActions;
        List<City> buildCities = state.
                getCityRepository().
                getCities().
                values().
                stream().     // loop all cities
                        filter(city -> {
                    Optional<CityCard> pc = getCurrentPlayer().getHand().stream().map(playerCard -> (CityCard) playerCard).filter(cityCard -> cityCard.getCity().equals(city)).findFirst();
                    boolean canBuild = pc.isPresent() && pathFinder.isDiscardableCard(pc.get(), getCurrentPlayer().getHand().indexOf(pc.get()));
                    if (canBuild) {
                        var path = pathFinder.getPath(city, true);
                        return pc.get().getCity().getResearchStation() == null && (city.equals(getCurrentCity()) || (path.size() > 0 && path.size() < 4));
                    }
                    return false;
                }).
                filter(possibleRS -> {
                    PathFinder3.WalkingPathFinder walkingPathFinder = new PathFinder3.WalkingPathFinder(state, getCurrentPlayer(), possibleRS);
                    // see if there's a RS in a min walking distance <= 3 we skip this city
                    for (ResearchStation existingRS : state.getCityRepository().getResearchStations()) {
                        List<ActionType.MovingAction> pathToExistingRs = walkingPathFinder.getPath(existingRS.getCity());
                        if (pathToExistingRs.size() != 0 && pathToExistingRs.size() <= 3) return false;
                        // note: we already checked for the city not having a RS,
                        // so the possibleRS city which has path size 0 (being the current city) doesn't have RS
                        // hence if the path is 0 it means the city is not reachable by walking (walking path size > 4)
                        // so : if min distance is <= 3 and not 0 we can't build a RS here
                    }
                    return true;
                }).
                collect(Collectors.toList());


        for (City buildable : buildCities) {
            List<ActionType.StandingAction> building = new ArrayList<>();
            building.add(new ActionType.StandingAction(ActionType.BUILD_RESEARCH_STATION, buildable));
            buildingActions.add(macro(pathFinder.getPath(buildable, true), building));
        }

        return buildingActions;
    }

    protected List<MacroAction> buildGiveKnowledgeMacroActions() {
        List<MacroAction> giveActions = new ArrayList<>();
        Player currentPlayer = state.getPlayerRepository().getCurrentPlayer();
        if (currentPlayer.getRole() instanceof Researcher) {
            return buildGiveKnowledgeMacroActionsResearcher();
        } else {
            currentPlayer.getHand().     // for all cards
                    stream().
                    map(pc -> (CityCard) pc).
                    map(CityCard::getCity).

                    forEach(cityInHand -> {   // see if the city is reachable
                        List<ActionType.MovingAction> path = pathFinder.lightestPath(cityInHand);
                        boolean exchangeNow = cityInHand.equals(currentPlayer.getCity()) || (path.size() > 0 && path.size() < 4);
                        //immediate give checks where players are
                        if (exchangeNow) {
                            //loop through all players and see if they are in that city, and exchange the card with them
                            state.getPlayerRepository().
                                    getPlayers().
                                    values().
                                    stream().
                                    filter(player -> !player.equals(currentPlayer)).
                                    filter(player -> player.getCity().equals(cityInHand)).
                                    map(player -> {
                                        List<ActionType.StandingAction> give = new ArrayList<>();
                                        give.add(new ActionType.StandingAction(ActionType.SHARE_KNOWLEDGE, player.getCity()));
                                        return macro(pathFinder.getPath(cityInHand, true), give);
                                    }).
                                    forEach(giveActions::add);
                        } else {
                            // give later finds just reachable and exchangeable cards
                            // add anyway the macro to wait for the other players
                            if (path.size() == 4)
                                giveActions.add(macro(path, new ArrayList<ActionType.StandingAction>()));
                        }
                    });

        }
        return giveActions;
    }


    protected List<MacroAction> buildTakeKnowledgeMacroActions() {
        List<MacroAction> takeActions = new ArrayList<>();
        Player currentPlayer = state.getPlayerRepository().getCurrentPlayer();

        state.getPlayerRepository().
                getPlayers().
                values().
                stream().
                filter(player -> !player.equals(currentPlayer)).
                forEach(player -> {
                    if (player.getRole() instanceof Researcher) {
                        if (player.getCity().equals(currentPlayer.getCity())) {    // we wait here because the researcher could give us any card if we are in the same city
                            takeActions.add(skipMacroAction(4, currentPlayer.getCity()));
                        } else {
                            // we find the path to reach her and wait
                            List<ActionType.MovingAction> path = pathFinder.lightestPath(player.getCity());
                            if (path.size() > 0 && path.size() <= 4) {
                                List<ActionType.StandingAction> skip = new ArrayList<>();
                                IntStream.range(path.size(), 4).forEach(i -> skip.add(new ActionType.StandingAction(ActionType.SKIP_ACTION, player.getCity())));
                                //takeActions.add(macro(path,skip));
                            }
                        }
                    } else {
                        player.getHand().     // for all cards in the other player hand
                                stream().
                                map(pc -> (CityCard) pc).
                                map(CityCard::getCity).
                                forEach(cityInHand -> {   // see if the city is reachable
                                    List<ActionType.MovingAction> path = pathFinder.lightestPath(cityInHand);
                                    boolean reachable = cityInHand.equals(currentPlayer.getCity()) || (path.size() > 0 && path.size() <= 4);
                                    if (reachable) {
                                        List<ActionType.StandingAction> skip =
                                                IntStream.range(path.size(), 4).
                                                        mapToObj(i -> new ActionType.StandingAction(ActionType.SKIP_ACTION, cityInHand)).
                                                        collect(Collectors.toList());
                                        takeActions.add(macro(path, skip));
                                    }
                                });
                    }
                });

        return takeActions;
    }

    protected List<MacroAction> buildGiveKnowledgeMacroActionsResearcher() {
        List<MacroAction> giveActions = new ArrayList<>();
        Player currentPlayer = state.getPlayerRepository().getCurrentPlayer();


        state.getCityRepository().
                getCities().
                values().
                forEach(city -> {
                    List<ActionType.MovingAction> path = pathFinder.lightestPath(city);
                    boolean reachableNow = city.equals(currentPlayer.getCity()) || (path.size() > 0 && path.size() < 4);
                    // if we can give now
                    if (reachableNow) {
                        // see if there's a player in that city
                        List<Player> otherPlayersInCity = city.getPawns().
                                stream().
                                filter(player -> !player.equals(currentPlayer)).
                                collect(Collectors.toList());

                        if (otherPlayersInCity.isEmpty()) {
                            List<ActionType.StandingAction> skip =
                                    IntStream.range(path.size(), 4).
                                            mapToObj(i -> new ActionType.StandingAction(ActionType.SKIP_ACTION, city)).
                                            collect(Collectors.toList());

                            //giveActions.add(macro(path,skip));
                        } else {
                            otherPlayersInCity.
                                    stream().
                                    map(player -> {
                                        List<ActionType.StandingAction> give = new ArrayList<>();
                                        give.add(new ActionType.StandingAction(ActionType.SHARE_KNOWLEDGE, player.getCity()));  //give best card
                                        return macro(path, give);
                                    }).forEach(giveActions::add);
                        }


                    } else {
                        if (path.size() == 4) giveActions.add(macro(path, new ArrayList<>()));
                    }
                });

        return giveActions;
    }

    protected List<MacroAction> buildWalkAwayMacroActions(int n) {
        return state.getCityRepository().
                getCities().
                values().
                stream().
                map(city -> pathFinder.getPath(city, false)).  // get the path for each city
                        filter(list -> list.size() == n).                               // see if it's distance 4 from current city
                        map(list -> macro(list, new ArrayList<>())).            // return a macro with the walking actions only
                        collect(Collectors.toList());
    }

    protected MacroAction fillMacroAction(MacroAction toFill) {
        int remaining = 4 - toFill.size();
        if (remaining == 0) return toFill;
        if (remaining < 0) throw new IllegalArgumentException();
        MacroAction filledMacro = null;
        Record lastAction = toFill.getAtIndex(3 - remaining);
        City lastReachedCity = null;
        if (lastAction instanceof ActionType.StandingAction lastApplied) {
            lastReachedCity = lastApplied.applyTo();
        } else if (lastAction instanceof ActionType.MovingAction lastReached) {
            lastReachedCity = lastReached.toCity();
        }
        if (lastReachedCity == null) throw new IllegalStateException("Error");
        try {

            IState forward = this.state.clone();
            currentPlayer = forward.getPlayerRepository().getPlayers().get(currentPlayer.getName());

            new MacroActionsExecutor().executeIndexedMacro(forward, toFill, false);

            var next =
                    initialise(forward, lastReachedCity, currentPlayer).
                            getActions().
                            stream().
                            filter(macro -> macro.size() == remaining).
                            findFirst().
                            orElse(MacroAction.skipMacroAction(remaining, currentPlayer.getCity()));

            filledMacro = combine(toFill, next);

        } catch (Exception e) {
            //System.out.println("Error when filling macro " + toFill + " -> " + e.getMessage());
            //e.printStackTrace();
        } finally {
            if (filledMacro == null) filledMacro = toFill;
            if (filledMacro.size() < 4) {
                combine(filledMacro, MacroAction.skipMacroAction(remaining, lastReachedCity));
                //System.out.println("Fixed : " + filledMacro);
            }
        }
        return filledMacro;
    }

    public List<MacroAction> getActions() {
        return actions == null ? actions = buildAllMacroActions() : actions;
    }

    protected Player getCurrentPlayer() {
        return this.currentPlayer == null ? this.state.getPlayerRepository().getCurrentPlayer() : this.currentPlayer;
    }

    protected City getCurrentCity() {
        return this.currentCity == null ? this.state.getPlayerRepository().getCurrentPlayer().getCity() : this.currentCity;
    }

    @SafeVarargs
    protected static List<String> logMacros(List<MacroAction>... lists) {
        List<String> log = new ArrayList<>();
        for (List<MacroAction> list : lists) {
            list.forEach(macro -> log.add(macro.toString()));
            log.add("\n");
        }
        return log;
    }
}
