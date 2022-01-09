package org.um.nine.headless.agents.rhea.pathfinder;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.rhea.state.StateEvaluation;
import org.um.nine.headless.agents.utils.Logger;
import org.um.nine.headless.agents.utils.Reporter;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.stream;
import static org.um.nine.headless.game.Settings.LOG;
import static org.um.nine.headless.game.Settings.ROUND_INDEX;
import static org.um.nine.headless.game.domain.ActionType.*;

public class PathFinder2 {
    final IState state;
    final Player currentPlayer;
    GCity currentCity;
    final List<GCity> costGraph;
    String path = "";
    String cardsInHand = "";

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
            Reporter graphReporter = new Reporter();
            graphReporter.getLog().add(cardsInHand);
            graphReporter.getLog().add("============================================================================================");
            for (GCity gc : this.costGraph) {
                if (gc.shortestPathFromCurrentCity.depth() > 0)
                    graphReporter.getLog().add(String.format("%1$-20s", gc.city.getName()) + gc.shortestPathFromCurrentCity.toString());
            }
            graphReporter.getLog().add("============================================================================================");
            graphReporter.report(path, true);
            Logger.addLog("City graph has been successfully reported ... \"" + path + "\"");
        }
    }

    public void evaluatePath() {
        if (LOG) {
            Logger.addLog("Evaluating city graph paths from current location : " + currentCity.city.getName());
            Logger.addLog(cardsInHand = "Cards in hand : " + currentPlayer.getHand().stream().
                    map(c -> c.getName() +
                            " " + ((CityCard) c).getCity().getColor()).
                    collect(Collectors.toList()));
            String header = "reports/round-" + ROUND_INDEX + "/pathFinder/";
            boolean folder = new File("src/main/resources/" + header).mkdirs();
            path = header + "pathReport" + "-" + currentPlayer.getName() + "-" + currentCity.city.getName() + ".txt";
        }

        currentCity.walkingDistance = 0;
        this.evaluateWalkingPath();
        this.buildAllPaths();
        this.evaluateShuttlePath();
        this.buildAllPaths();
        this.evaluateDirectPath();
        this.buildAllPaths();
        this.evaluateCharterPath();
        this.buildAllPaths();
        this.logGraph();
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
        double ac_new = StateEvaluation.abilityCure2(state, cc.getCity().getColor());
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
        cardsLoop:
        for (int i = 0; i < currentPlayer.getHand().size(); i++) {
            if (currentPlayer.getHand().get(i) instanceof CityCard cc) {
                if (isDiscardableCard(cc, ac[i], i)) {
                    // we have a card, from there we can move wherever we want
                    //if we can get to that city and still have at least 1 move left
                    GCity fromCharter = findGCity(cc.getCity());
                    int depth = fromCharter.shortestPathFromCurrentCity.depth();
                    //easy check, if we want to charter now from city X, and to get to X we used direct flight
                    //it means we used card with city X
                    //TODO: implement better strategy to see if there was another path

                    if (depth > 0 && depth < 4) {
                        if (fromCharter.shortestPathFromCurrentCity.path[depth - 1].action().equals(DIRECT_FLIGHT))
                            continue cardsLoop;
                        // check each city which wasn't accessible before
                        List<GCity> charterFlightCities = this.costGraph.stream().
                                filter(gc -> gc.shortestPathFromCurrentCity.depth() == 0).
                                collect(Collectors.toList());

                        for (GCity charterFlightCity : charterFlightCities) {
                            charterFlightCity.prev = fromCharter;
                            charterFlightCity.prevA = CHARTER_FLIGHT;
                            charterFlightCity.walkingDistance = depth + 1;  //take charter flight action step + previous depth
                            if (charterFlightCity.walkingDistance < 4) {
                                this.currentCity = charterFlightCity;
                                this.evaluateWalkingPath();
                                this.currentCity.marked = true;
                            } else createShortestPath(charterFlightCity);


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
            evaluatePostShuttlePath(researchStations, prev);
        } else {
            List<GCity> reachableWalking = state.
                    getCityRepository().
                    getCities().
                    values().
                    stream().
                    filter(city -> city.getResearchStation() != null).
                    map(this::findGCity).
                    filter(gCity -> gCity.shortestPathFromCurrentCity.depth() < 3).
                    collect(Collectors.toList());

            for (GCity reachable : reachableWalking) {


                List<GCity> researchStations = state.
                        getCityRepository().
                        getCities().
                        values().
                        stream().
                        filter(city -> city.getResearchStation() != null).
                        filter(city -> city != reachable.city).
                        map(this::findGCity).
                        collect(Collectors.toList());


                evaluatePostShuttlePath(researchStations, reachable);
            }
        }
    }

    private void evaluatePostShuttlePath(List<GCity> researchStations, GCity prev) {
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
                if (v.walkingDistance > d && !v.marked && v != u.prev) {
                    v.walkingDistance = d + 1;
                    v.prev = u;
                    v.prevA = DRIVE;
                }
            }
        }

    }

    private Path createShortestPath(GCity gc) {
        List<MovingAction> m = new ArrayList<>();
        GCity current = gc;
        while (current.prev != null) {
            m.add(new MovingAction(current.prevA, current.prev.city, current.city));
            current = current.prev;
        }

        if (m.size() > 0 && m.get(0).action().equals(CHARTER_FLIGHT)) {
            var mm = stream(current.shortestPathFromCurrentCity.path()).collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(mm);
            m.addAll(mm);
        }
        Collections.reverse(m);

        Path newPath = new Path(m.toArray(new MovingAction[4]));

        //gc.lightestPathFromCurrentCity = lightest(gc.shortestPathFromCurrentCity, newPath);
        gc.shortestPathFromCurrentCity = shortest(gc.shortestPathFromCurrentCity, newPath);


        return gc.shortestPathFromCurrentCity;
    }

    private Path lightest(Path p1, Path p2) {
        long p1Depth = stream(p1.path()).filter(ma -> (ma.action().equals(DIRECT_FLIGHT) || ma.action().equals(CHARTER_FLIGHT))).count();
        long p2Depth = stream(p2.path()).filter(ma -> (ma.action().equals(DIRECT_FLIGHT) || ma.action().equals(CHARTER_FLIGHT))).count();
        return p1Depth > p2Depth ? p2 : p1;
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


    private void buildAllPaths() {
        for (GCity gc : this.costGraph) {
            createShortestPath(gc);
        }
    }

    public Path getShortestPath(City c) {
        GCity gc = findGCity(c);
        return gc.shortestPathFromCurrentCity == null ? createShortestPath(gc) : gc.shortestPathFromCurrentCity;
    }

    public List<MovingAction> shortestPath(City c) {
        GCity gc = findGCity(c);
        Path p = gc.shortestPathFromCurrentCity == null ? createShortestPath(gc) : gc.shortestPathFromCurrentCity;
        return Arrays.stream(p.path()).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }

    public boolean isSpendingCard(List<MovingAction> shortestPath, City city) {
        for (MovingAction action : shortestPath) {
            if (action.action().equals(DIRECT_FLIGHT))
                return action.toCity().equals(city);
            if (action.action().equals(CHARTER_FLIGHT))
                return action.fromCity().equals(city);
        }
        return false;
    }


    private static class GCity {
        private final City city;
        private GCity prev;
        private boolean marked;
        private ActionType prevA;
        private int walkingDistance;
        private Path shortestPathFromCurrentCity;
        private Path lightestPathFromCurrentCity;

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
