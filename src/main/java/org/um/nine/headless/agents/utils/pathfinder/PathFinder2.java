package org.um.nine.headless.agents.utils.pathfinder;

import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.state.StateEvaluation;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.ActionType.*;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.stream;
import static org.um.nine.headless.agents.utils.Log.log;
import static org.um.nine.headless.agents.utils.Log.record;
import static org.um.nine.headless.game.Settings.LOG;
import static org.um.nine.headless.game.domain.ActionType.*;

public class PathFinder2 {
    final IState state;
    final Player currentPlayer;
    GCity currentCity;
    final List<GCity> costGraph;
    final List<GCity> visitedCities = new ArrayList<>();


    public PathFinder2(IState state, City currentCity, Player currentPlayer) {
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));

        this.currentPlayer = currentPlayer;
        this.currentCity = findGCity(currentCity);
        this.currentCity.marked = true;
        this.evaluatePath();
    }

    private void logGraph() {
        if (LOG) {
            record("============================================================================================");
            for (GCity gc : this.costGraph) {
                if (gc.shortestPathFromCurrentCity.depth() > 0)
                    record(String.format("%1$-20s", gc.city.getName()) + gc.shortestPathFromCurrentCity.toString());
            }
            record("============================================================================================");
            log();
        }
    }

    public void evaluatePath() {
        currentCity.walkingDistance = 0;
        System.out.println(currentPlayer.getHand());
        this.evaluateWalkingPath();
        logGraph();
        this.evaluateShuttlePath();
        logGraph();
        this.evaluateDirectPath();
        logGraph();
        this.evaluateCharterPath();
        logGraph();
    }

    private double[] evaluateAbilityCureOfCards() {
        double[] ac = new double[currentPlayer.getHand().size()];
        for (int i = 0; i < currentPlayer.getHand().size(); i++) {
            PlayerCard pc = currentPlayer.getHand().get(i);
            if (pc instanceof CityCard cc) {
                ac[i] = StateEvaluation.abilityCure(state, cc.getCity().getColor());
            }
        }
        return ac;
    }

    private CityCard getOneDiscardableCard(double[] ac) {
        CityCard discarding = null;
        for (int i = 0; i < ac.length; i++) {
            CityCard cc = ((CityCard) currentPlayer.getHand().get(i));
            if (isDiscardableCard(cc, ac[i], i)) {
                discarding = cc;
                break;
            }
        }
        return discarding;
    }

    private boolean isDiscardableCard(CityCard cc, double ac, int i) {
        // we remove it to check if discarding wouldn't affect ability to cure disease
        currentPlayer.getHand().remove(cc);
        double ac_new = StateEvaluation.abilityCure(state, cc.getCity().getColor());
        currentPlayer.getHand().add(i, cc);
        return ac_new == ac;
    }

    private void evaluateDirectPath() {
        double[] ac = evaluateAbilityCureOfCards();
        CityCard discarding = getOneDiscardableCard(ac);

        if (discarding != null) {
            GCity directFlightGCity = findGCity(discarding.getCity());
            directFlightGCity.prev = currentCity;
            directFlightGCity.prevA = DIRECT_FLIGHT;
            directFlightGCity.walkingDistance = 1;  //take direct flight action step
            this.currentCity = directFlightGCity;
            this.evaluateWalkingPath();
            this.currentCity.marked = true;
        }
    }

    private void evaluateCharterPath() {
        double[] ac = evaluateAbilityCureOfCards();
        for (int i = 0; i < currentPlayer.getHand().size(); i++) {
            if (currentPlayer.getHand().get(i) instanceof CityCard cc) {
                if (isDiscardableCard(cc, ac[i], i)) {
                    // we have a card, from there we can move wherever we want
                    //if we can get to that city and still have at least 1 move left
                    GCity fromCharter = findGCity(cc.getCity());
                    int depth = fromCharter.shortestPathFromCurrentCity.depth();
                    if (depth > 0 && depth < 4) {
                        // check each city which wasn't accessible before
                        List<GCity> charterFlightCities = this.costGraph.stream().
                                filter(gc -> gc.shortestPathFromCurrentCity.depth() == 0).
                                collect(Collectors.toList());

                        for (GCity charterFlightCity : charterFlightCities) {
                            charterFlightCity.prev = fromCharter;
                            charterFlightCity.prevA = CHARTER_FLIGHT;
                            charterFlightCity.walkingDistance = depth + 1;  //take charter flight action step + previous depth
                            this.currentCity = charterFlightCity;
                            this.evaluateWalkingPath();
                            this.currentCity.marked = true;
                        }
                    }
                }
            }
        }
    }

    private void evaluateShuttlePath() {
        if (currentCity.city.getResearchStation() != null) {
            List<GCity> researchStations = state.
                    getCityRepository().
                    getCities().
                    values().
                    stream().
                    filter(city -> city.getResearchStation() != null).
                    filter(city -> city != currentCity.city).
                    map(this::findGCity).
                    collect(Collectors.toList());

            GCity prev = this.currentCity;
            for (GCity researchStationNeighbour : researchStations) {
                researchStationNeighbour.prev = prev;
                researchStationNeighbour.prevA = SHUTTLE;
                researchStationNeighbour.walkingDistance = 1;  //take shuttle action step
                this.currentCity = researchStationNeighbour;
                this.evaluateWalkingPath();
                this.currentCity.marked = true;
            }
            this.currentCity = prev;
        }
    }

    private void evaluateWalkingPath() {
        Comparator<GCity> comparator = Comparator.comparingInt(c -> c.city.getCubes().size());  // ;)
        PriorityQueue<GCity> Q = new PriorityQueue<>(comparator.reversed());
        for (GCity gc : this.costGraph) {
            if (gc != currentCity) {
                gc.prev = null;
                gc.walkingDistance = MAX_VALUE;
            }
            Q.add(gc);
        }

        GCity u;

        while (!Q.isEmpty()) {
            u = Q.poll();
            for (City c : u.city.getNeighbors()) {
                GCity v = findGCity(c);
                int d = u.walkingDistance;
                if (v.walkingDistance > d && !v.marked) {
                    v.walkingDistance = d + 1;
                    v.prev = u;
                    v.prevA = DRIVE;
                }
            }
        }

        for (GCity gc : this.costGraph) {
            createShortestPath(gc);
        }
    }

    private Path createShortestPath(GCity gc) {
        List<MovingAction> m = new ArrayList<>();
        GCity current = gc;
        while (current.prev != null) {
            m.add(new MovingAction(current.prevA, current.prev.city, current.city));
            current = current.prev;
        }
        Collections.reverse(m);
        gc.shortestPathFromCurrentCity = shortest(gc.shortestPathFromCurrentCity, new Path(m.toArray(new MovingAction[4])));
        return gc.shortestPathFromCurrentCity;
    }

    private Path shortest(Path oldPath, Path newPath) {

        if (oldPath == null || (oldPath.depth() == 0 && newPath.depth() > 0)) {
            return newPath;
        } else if (oldPath.depth() > 0 && newPath.depth() == 0) {
            return oldPath;
        }

        if (newPath.depth() > 0 && newPath.path()[0].action().equals(CHARTER_FLIGHT)) {
            return combine(oldPath, newPath);
        }

        return oldPath.depth() < newPath.depth() ? oldPath : newPath;
    }

    private Path combine(Path oldPath, Path newPath) {
        //we get here only if oldPath.depth is not 0 , therefore we need to check if charter flight would give
        // us a shorter path than the one already evaluated
        GCity startCharter = findGCity(newPath.path()[0].fromCity());
        if (startCharter.shortestPathFromCurrentCity.depth() + newPath.depth() < oldPath.depth()) {
            MovingAction[] ma = new MovingAction[4];
            if (startCharter.shortestPathFromCurrentCity.depth() >= 0)
                System.arraycopy(startCharter.shortestPathFromCurrentCity.path, 0, ma, 0, startCharter.shortestPathFromCurrentCity.depth() + newPath.depth());
            int k = 0;
            for (int i = startCharter.shortestPathFromCurrentCity.depth(); i < 4; i++) {
                ma[i] = newPath.path[k];
                k++;
            }
            return new Path(ma);
        }
        return oldPath;
    }


    public Path getShortestPath(City c) {
        GCity gc = findGCity(c);
        return gc.shortestPathFromCurrentCity == null ? createShortestPath(gc) : gc.shortestPathFromCurrentCity;
    }

    private GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }


    private static class GCity {
        private final City city;
        private GCity prev;
        private boolean marked;
        private ActionType prevA;
        private int walkingDistance;
        private Path shortestPathFromCurrentCity;

        private GCity(City c) {
            this.city = c;
        }

        @Override
        public String toString() {
            return city.getName();
        }
    }

    private static record Path(MovingAction[] path) {
        int depth() {
            return (int) stream(path).filter(Objects::nonNull).count();
        }

        @Override
        public String toString() {
            return Arrays.stream(path).filter(Objects::nonNull).collect(Collectors.toList()).toString();
        }
    }


    public static void main(String[] args) {
        IState state = GameStateFactory.createInitialState();
        try {
            state.getCityRepository().addResearchStation(state.getCityRepository().getCities().get("Tokyo"));
            state.getCityRepository().addResearchStation(state.getCityRepository().getCities().get("Cairo"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        PathFinder2 pf = new PathFinder2(
                state,
                GameStateFactory.getInitialState().getCityRepository().getCities().get("Atlanta"),
                GameStateFactory.getInitialState().getPlayerRepository().getCurrentPlayer()
        );

    }


}
