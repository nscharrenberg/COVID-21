package org.um.nine.headless.agents.utils;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.state.StateEvaluation;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;


public class PathFinder {

    private final IState state;
    private final List<GCity> costGraph;
    private final List<GCity> visitedCities = new ArrayList<>();

    private PathFinder(IState state) {
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));
    }

    public final void evaluateCostGraph() {
        this.evaluateCostGraphWalking();
        this.evaluateCostGraphShuttleFlight();
        this.evaluateCostGraphDirectFlight();
        this.evaluateCostGraphCharterFlight();
    }

    private void evaluateCostGraphWalking() {
        GCity currentCity = getCurrentCity();
        int dist = 0;
        if (currentCity != null) {
            visitedCities.clear();
            currentCity.walkingPath.walkingActionDepth = dist;
            visitedCities.add(currentCity);
            for (int i = 0; i < visitedCities.size(); i++) {
                currentCity = visitedCities.get(i);
                dist = currentCity.walkingPath.walkingActionDepth < 4 ? currentCity.walkingPath.walkingActionDepth + 1 : MAX_VALUE;
                setNeighbourWalkingDistances(currentCity, dist);
            }
        }
    }

    private void evaluateCostGraphCharterFlight() {
        List<CityCard> citiesInHand = new ArrayList<>();
        Player p = state.getPlayerRepository().getCurrentPlayer();
        for (PlayerCard pc : p.getHand()) {
            if (pc instanceof CityCard) citiesInHand.add((CityCard) pc);
        }

        removeValuableCards(p, citiesInHand);

        int bestCard = 0;
        int currentMin = MAX_VALUE;
        //find the closest valid city
        for (int i = 0; i < citiesInHand.size(); i++) {
            GCity city = findGCity(citiesInHand.get(i).getCity());
            if (city.walkingPath.walkingActionDepth < currentMin) {
                currentMin = city.walkingPath.walkingActionDepth;
                bestCard = i;
            }
        }

        for (GCity gc : costGraph) {
            gc.charterFlightPath.discardedCard = citiesInHand.size() > 0 ? citiesInHand.get(bestCard) : null;
            gc.charterFlightPath.charterFlightActionDepth = currentMin + 1 <= 4 ? currentMin + 1 : MAX_VALUE;
        }
    }

    public void removeValuableCards(Player p, List<CityCard> cities) {
        for (int i = 0; i < cities.size(); i++) {
            Color c = cities.get(i).getCity().getColor();
            if (!state.getDiseaseRepository().getCures().get(c).isDiscovered() &&
                    p.equals(getMaxAcPlayer(c))) {
                cities.remove(i);
                i--;
            }
        }
    }

    private void evaluateCostGraphDirectFlight() {
        List<CityCard> citiesInHand = new ArrayList<>();
        Player p = state.getPlayerRepository().getCurrentPlayer();
        for (PlayerCard pc : p.getHand()) {
            if (pc instanceof CityCard) citiesInHand.add((CityCard) pc);
        }

        // remove city cards that would reduce chance of curing disease when discarded
        removeValuableCards(p, citiesInHand);

        if (citiesInHand.isEmpty()) { //if we don't have any cards that we can discard
            for (GCity gc : this.costGraph) {
                gc.directFlightPath.discardedCard = null;
                gc.directFlightPath.directFlightActionDepth = MAX_VALUE;
            }
            return;
        }

        for (GCity gc : this.costGraph) {
            gc.setNActionsDirectFlight(new ArrayList<>(Collections.nCopies(citiesInHand.size(), -1)));
        }

        for (int i = 0; i < citiesInHand.size(); i++) {
            //we fly to cityCard destination and start walking from there
            evaluateCostGraphPostDirectFlight(findGCity(citiesInHand.get(i).getCity()), i);
        }

        //after evaluating the distances for all cities and available cards we pick the card with the shortest path
        for (GCity gc : this.costGraph) {
            int bestCard = 0;
            int currentMin = MAX_VALUE;
            for (int i = 0; i < gc.directFlightActionsDepthList.size(); i++) {
                int dist = gc.directFlightActionsDepthList.get(i);
                if (dist < currentMin) {
                    currentMin = dist;
                    bestCard = i;
                }
            }
            gc.directFlightPath.discardedCard = citiesInHand.size() > 0 ? citiesInHand.get(bestCard) : null;
            gc.directFlightPath.directFlightActionDepth = currentMin;
        }
    }

    private void evaluateCostGraphPostDirectFlight(GCity city, int cityCardIndex) {
        if (city != null) {
            visitedCities.clear();
            city.directFlightActionsDepthList.set(cityCardIndex, 1);
            visitedCities.add(city);

            for (int i = 0; i < visitedCities.size(); i++) {
                city = visitedCities.get(i);
                int currentDist = city.directFlightActionsDepthList.get(cityCardIndex);
                int dist = currentDist < 4 ? currentDist + 1 : MAX_VALUE;
                setNeighbourDirectFlightDistances(city, dist, cityCardIndex);
            }
        }
    }

    private void evaluateCostGraphShuttleFlight() {
        GCity closestStation = this.costGraph.stream().
                filter(gc -> gc.city.getResearchStation() != null &&
                        gc.walkingPath.walkingActionDepth <= 3).

                findFirst().orElse(null);

        //must have done 3 actions at most to get to the closest research station
        //shuttle will spend one action

        if (closestStation != null) {
            List<GCity> shuttleCities = this.costGraph.stream().
                    filter(gc -> gc.city.getResearchStation() != null && !gc.city.equals(closestStation.city)).
                    collect(Collectors.toList());


            for (GCity shuttleCity : shuttleCities) {
                shuttleCity.shuttlePath.shuttle = new ActionType.MovingAction(ActionType.SHUTTLE, closestStation.city, shuttleCity.city);
                evaluateCostGraphPostShuttleFlight(shuttleCity, closestStation.walkingPath.walkingActionDepth + 1);
            }

        }
    }


    private void evaluateCostGraphPostShuttleFlight(GCity city, int dist) {
        visitedCities.clear();

        // if by shuttling from another city we get there in less actions :

        if (city.shuttlePath.shuttleActionDepth == -1 || dist < city.shuttlePath.shuttleActionDepth) {  //shuttleActionDepth starts at -1
            city.shuttlePath.shuttleActionDepth = dist;
            visitedCities.add(city);
            for (int i = 0; i < visitedCities.size(); i++) {
                city = visitedCities.get(i);
                int currentDist = city.shuttlePath.shuttleActionDepth;
                dist = currentDist < 4 ? currentDist + 1 : MAX_VALUE;
                setNeighbourPostShuttleDistance(city, dist);
            }
        }
    }


    private void setNeighbourWalkingDistances(GCity currentCity, int dist) {
        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (gc.walkingPath.walkingActionDepth == -1) {
                gc.walkingPath.walkingActionDepth = dist;
                gc.walkingPath.walking = new ActionType.MovingAction(ActionType.DRIVE, currentCity.city, gc.city);
                visitedCities.add(gc);
            }
        }
    }

    private void setNeighbourPostShuttleDistance(GCity currentCity, int dist) {
        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (gc.shuttlePath.shuttleActionDepth == -1 || dist < gc.shuttlePath.shuttleActionDepth) {
                gc.shuttlePath.shuttleActionDepth = dist;
                gc.shuttlePath.postShuttleWalking = new ActionType.MovingAction(ActionType.DRIVE, currentCity.city, gc.city);
                visitedCities.add(gc);
            }
        }
    }

    private void setNeighbourDirectFlightDistances(GCity currentCity, int dist, int cityCardIndex) {
        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (gc.directFlightActionsDepthList.get(cityCardIndex) == -1) {
                gc.directFlightActionsDepthList.set(cityCardIndex, dist);
                visitedCities.add(gc);
            }
        }
    }

    public GCity getCurrentCity() {
        return this.costGraph.stream().filter(gc -> gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity())).findFirst().orElse(null);
    }

    public GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }

    private Player getMaxAcPlayer(Color t) {
        double maxAc = 0;
        Player maxP = null;
        for (Player p : state.getPlayerRepository().getPlayerOrder()) {
            double ac = Ac(p, t);
            maxAc = Math.max(maxAc, ac);
            maxP = p;
        }
        return maxP;
    }

    private double Ac(Player p, Color t) {
        double hpt = p.getHand().
                stream().filter(pc ->
                pc instanceof CityCard cc &&
                        cc.getCity().getColor()
                                .equals(t)).
                count();
        double cd = StateEvaluation.Cd(p);
        if (hpt >= cd) return 1;
        else return hpt / cd;
    }


    /**
     * Everything on this instance is based on the current city given in the state at the initialization
     */
    public static class Descriptor extends PathFinder {
        public Descriptor(IState state) {
            super(state);
            super.evaluateCostGraph();
        }

        private List<ActionType.MovingAction> buildWalkPath(GCity gc, int walkingActionDepth) {
            List<ActionType.MovingAction> actions = new ArrayList<>();
            if (gc.walkingPath.walking != null && (walkingActionDepth <= 4) && (walkingActionDepth > 0)) {
                for (int i = 0; i < walkingActionDepth; i++) {
                    actions.add(new ActionType.MovingAction(ActionType.DRIVE, gc.walkingPath.walking.fromCity(), gc.city));
                    gc = findGCity(gc.walkingPath.walking.fromCity());
                }
            }
            return actions;
        }

        private Map.Entry<List<ActionType.MovingAction>, GCity> buildWalkPathPostShuttle(GCity gc, int walkingActionDepth) {
            List<ActionType.MovingAction> actions = new ArrayList<>();
            if (gc.shuttlePath.postShuttleWalking != null) {
                for (int i = 0; i < walkingActionDepth; i++) {
                    if (gc.shuttlePath.shuttle != null) break;
                    actions.add(new ActionType.MovingAction(ActionType.DRIVE, gc.shuttlePath.postShuttleWalking.fromCity(), gc.city));
                    gc = findGCity(gc.shuttlePath.postShuttleWalking.fromCity());
                }
            }
            return Map.entry(actions, gc);
        }

        private List<ActionType.MovingAction> buildShuttlePath(GCity gc) {
            List<ActionType.MovingAction> shuttlePath = new ArrayList<>();
            if (gc.shuttlePath.shuttleActionDepth > 0 && gc.shuttlePath.shuttleActionDepth <= 4) {
                Map.Entry<List<ActionType.MovingAction>, GCity> path = buildWalkPathPostShuttle(gc, gc.shuttlePath.shuttleActionDepth - 1);
                shuttlePath = path.getKey();
                gc = path.getValue();
                shuttlePath.add(new ActionType.MovingAction(ActionType.SHUTTLE, gc.shuttlePath.shuttle.fromCity(), gc.city));
                gc = findGCity(gc.shuttlePath.shuttle.fromCity());
                shuttlePath.addAll(buildWalkPath(gc, gc.walkingPath.walkingActionDepth));
            }
            return shuttlePath;
        }

        private List<ActionType.MovingAction> buildDirectFlightPath(GCity gc) {
            List<ActionType.MovingAction> directFlightPath = new ArrayList<>();
            if (gc.directFlightPath.directFlightActionDepth > 0 && gc.directFlightPath.directFlightActionDepth <= 4) {
                directFlightPath.add(new ActionType.MovingAction(ActionType.DIRECT_FLIGHT, getCurrentCity().city, gc.directFlightPath.discardedCard.getCity()));
                directFlightPath.addAll(buildWalkPath(gc, gc.walkingPath.walkingActionDepth));
            }
            return directFlightPath;
        }

        private List<ActionType.MovingAction> buildCharterFlightPath(GCity gc) {
            List<ActionType.MovingAction> charterFlightPath = new ArrayList<>();
            if (gc.charterFlightPath.charterFlightActionDepth > 0 && gc.charterFlightPath.charterFlightActionDepth <= 4) {
                GCity charterCity = findGCity(gc.charterFlightPath.discardedCard.getCity());
                charterFlightPath.addAll(buildWalkPath(charterCity,charterCity.walkingPath.walkingActionDepth));
                charterFlightPath.add(new ActionType.MovingAction(ActionType.CHARTER_FLIGHT, charterCity.city, gc.city));
            }
            return charterFlightPath;
        }

        public List<ActionType.MovingAction> shortestPath(City toCity) {
            GCity dest = findGCity(toCity);
            if (getCurrentCity().city.equals(dest.city)) return new ArrayList<>();

            if (dest.shortestWalkingPath == null) {
                dest.shortestWalkingPath = buildWalkPath(dest, dest.walkingPath.walkingActionDepth);
            }
            if (dest.shortestShuttlingPath == null) {
                dest.shortestShuttlingPath = buildShuttlePath(dest);
            }
            if (dest.shortestDirectFlightPath == null) {
                dest.shortestDirectFlightPath = buildDirectFlightPath(dest);
            }
            if (dest.shortestCharterFlightPath == null) {
                dest.shortestCharterFlightPath = buildCharterFlightPath(dest);
            }


            if (dest.shortestWalkingPath.isEmpty() && dest.shortestShuttlingPath.isEmpty() && dest.shortestCharterFlightPath.isEmpty() && dest.shortestDirectFlightPath.isEmpty()) return new ArrayList<>();
            int walkLength = dest.shortestWalkingPath.isEmpty() ? MAX_VALUE : dest.shortestWalkingPath.size();
            int shuttleLength = dest.shortestShuttlingPath.isEmpty() ? MAX_VALUE : dest.shortestShuttlingPath.size();
            int charterLength = dest.shortestCharterFlightPath.isEmpty() ? MAX_VALUE : dest.shortestCharterFlightPath.size();
            int directLength = dest.shortestDirectFlightPath.isEmpty() ? MAX_VALUE : dest.shortestDirectFlightPath.size();

            if ((walkLength <= shuttleLength) && (walkLength <= dest.shortestCharterFlightPath.size()) && (walkLength <= directLength)) {
                return dest.shortestWalkingPath;
            } else if ((shuttleLength <= dest.shortestCharterFlightPath.size()) && (shuttleLength <= directLength)) {
                return dest.shortestShuttlingPath;
            } else if ((charterLength <= directLength)) {
                return dest.shortestCharterFlightPath;
            } else{
                return dest.shortestDirectFlightPath;
            }
        }
    }

    private static class ShuttlePath {
        private ActionType.MovingAction shuttle;
        private ActionType.MovingAction postShuttleWalking;
        private int shuttleActionDepth;
    }

    private static class WalkingPath {
        private ActionType.MovingAction walking;
        private int walkingActionDepth;
    }

    private static class DirectFlightPath {
        private ActionType.MovingAction directFlight;
        private int directFlightActionDepth;
        private CityCard discardedCard;
    }

    private static class CharterFlightPath {
        private ActionType.MovingAction charterFlight;
        private int charterFlightActionDepth;
        private CityCard discardedCard;
    }

    private static class GCity {
        private final City city;
        private final ShuttlePath shuttlePath = new ShuttlePath();
        private final WalkingPath walkingPath = new WalkingPath();
        private final DirectFlightPath directFlightPath = new DirectFlightPath();
        private final CharterFlightPath charterFlightPath = new CharterFlightPath();
        private List<Integer> directFlightActionsDepthList; //different value depending on which city card you use

        private List<ActionType.MovingAction> shortestWalkingPath, shortestShuttlingPath, shortestDirectFlightPath, shortestCharterFlightPath;

        private GCity(City city) {
            this.city = city;
            this.walkingPath.walkingActionDepth = -1;
            this.shuttlePath.shuttleActionDepth = -1;
        }

        private void setNActionsDirectFlight(List<Integer> nActionsDirectFlight) {
            this.directFlightActionsDepthList = nActionsDirectFlight;
        }
    }

}
