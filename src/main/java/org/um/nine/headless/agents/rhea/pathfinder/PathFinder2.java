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
        this.evaluatePath();
    }

    private void logGraph() {
        if (LOG) {
            Reporter graphReporter = new Reporter();
            graphReporter.getLog().add(cardsInHand);
            graphReporter.getLog().add("============================================================================================");
            for (GCity gc : this.costGraph) {
                if (gc.shortestPathFromCurrentCity.depth() > 0) {
                    graphReporter.getLog().add(String.format("%1$-20s", gc.city.getName()) + gc.shortestPathFromCurrentCity.toString());
                    if (gc.lightestPathFromCurrentCity != null && !(gc.lightestPathFromCurrentCity.equals(gc.shortestPathFromCurrentCity))) {
                        graphReporter.getLog().add(String.format("%1$-20s", gc.city.getName()) + gc.lightestPathFromCurrentCity.toString());
                    }
                }
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

        GCity current = currentCity;
        currentCity.walkingDistance = 0;
        currentCity.marked = true;
        this.evaluateWalkingPath();
        this.buildAllPaths();
        currentCity = current;
        this.evaluateShuttlePath();
        currentCity = current;
        this.buildAllPaths();
        currentCity = current;
        this.evaluateDirectPath();
        currentCity = current;
        this.buildAllPaths();
        currentCity = current;
        this.evaluateCharterPath();
        currentCity = current;
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
            directFlightGCity.walkingDistance = currentCity.walkingDistance + 1;  //take direct flight action step
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
                    List<MovingAction> shortest = getPath(cc.getCity(), true);

                    int depth = shortest.size();

                    //easy check, if we want to charter now from city X, and to get to X we used direct flight
                    //it means we used card with city X
                    //TODO: implement better strategy to see if there was another path

                    if (depth > 0 && depth < 4) {
                        // check each city which wasn't accessible before
                        List<GCity> charterFlightCities = this.costGraph.stream().
                                filter(gc -> gc != currentCity && getPath(gc.city, true).size() == 0).
                                collect(Collectors.toList());

                        for (GCity charterFlightCity : charterFlightCities) {
                            charterFlightCity.prev = fromCharter;
                            charterFlightCity.prevA = CHARTER_FLIGHT;
                            charterFlightCity.walkingDistance = depth + 1;  //take charter flight action step + previous depth
                            if (charterFlightCity.walkingDistance < 4) {
                                this.currentCity = charterFlightCity;
                                this.evaluateWalkingPath();
                                this.currentCity.marked = true;
                            } else createShortestPath(charterFlightCity, shortest);
                        }
                    }
                }
            }
        }
    }

    private void createShortestPath(GCity charterFlightCity, List<MovingAction> shortest) {
        Collections.reverse(shortest);
        MovingAction[] ma = new MovingAction[4];
        for (int i = 0; i < shortest.size(); i++) ma[i] = shortest.get(i);
        ma[3] = new MovingAction(charterFlightCity.prevA, charterFlightCity.prev.city, charterFlightCity.city);
        charterFlightCity.shortestPathFromCurrentCity = new Path(ma);
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
            prev.marked = true;
            evaluatePostShuttlePath(researchStations, prev, prev.shortestPathFromCurrentCity);
        } else {
            currentCity.marked = true;
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


                Path p = getShortestPath(reachable.city);
                evaluatePostShuttlePath(researchStations, reachable, p);
                reachable.marked = true;
            }
        }
    }

    public List<MovingAction> getPath(City c, boolean needCardCityC) {
        List<MovingAction> shortest = shortestPath(c);
        if (needCardCityC && isSpendingCard(shortest, c)) return lightestPath(c);
        return shortest;
    }

    private void evaluatePostShuttlePath(List<GCity> researchStations, GCity prev, Path prevPath) {
        for (GCity researchStationNeighbour : researchStations) {
            researchStationNeighbour.prev = prev;
            researchStationNeighbour.prevA = SHUTTLE;
            researchStationNeighbour.walkingDistance = prev.walkingDistance + 1;  //take shuttle action step
            this.currentCity = researchStationNeighbour;
            this.evaluateWalkingPath();
            int depth = prevPath.depth();
            if (depth > 0) {
                for (int i = 0; i < depth; i++) {
                    prev.prev = findGCity(prevPath.path()[depth - (i + 1)].fromCity());
                    prev.prevA = prevPath.path()[depth - (i + 1)].action();
                    prev = prev.prev;
                }
            } else {
                prev.prev = null;
                prev.prevA = null;
            }

            this.currentCity.marked = true;
        }
        this.currentCity = prev;
    }

    private void evaluateWalkingPath() {
        Comparator<GCity> comparator = Comparator.comparingInt(c -> c.city.getCubes().size());  // ;)
        PriorityQueue<GCity> Q = new PriorityQueue<>(comparator.reversed());


        for (GCity gc : this.costGraph) {
            if (gc != currentCity && !gc.marked) {
                gc.prev = null;
                gc.walkingDistance = MAX_VALUE;
            }
        }

        GCity u;
        Q.add(currentCity);

        while (!Q.isEmpty()) {
            u = Q.poll();

            for (City c : u.city.getNeighbors()) {
                GCity v = findGCity(c);
                int d = u.walkingDistance + 1;

                if (v.walkingDistance > d && !v.marked) {
                    Q.remove(v);
                    v.walkingDistance = d;
                    v.prev = u;
                    v.prevA = DRIVE;
                    Q.add(v);
                }
            }
        }

    }

    private Path createShortestPath(GCity gc) {
        List<MovingAction> m = new ArrayList<>();
        GCity current = gc;
        while (current.prev != null && !current.city.equals(currentCity.city)) {
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

        Path shortest = shortest(gc.shortestPathFromCurrentCity, newPath);
        Path lightest = lightest(gc.shortestPathFromCurrentCity, lightest(gc.lightestPathFromCurrentCity, newPath));

        gc.shortestPathFromCurrentCity = shortest.depth() > 4 ? new Path(new MovingAction[0]) : shortest;
        if (lightest != null)
            gc.lightestPathFromCurrentCity = lightest.depth() > 4 ? new Path(new MovingAction[0]) : lightest;


        return gc.shortestPathFromCurrentCity;
    }

    private Path lightest(Path p1, Path p2) {
        if (p1 == null) return p2;
        if (p2 == null) return p1;
        if (!checkPath(p1) && checkPath(p2)) return p2;
        if (!checkPath(p2) && checkPath(p1)) return p1;
        long p1Depth = stream(p1.path()).filter(ma -> ma != null && (ma.action().equals(DIRECT_FLIGHT) || ma.action().equals(CHARTER_FLIGHT))).count();
        long p2Depth = stream(p2.path()).filter(ma -> ma != null && (ma.action().equals(DIRECT_FLIGHT) || ma.action().equals(CHARTER_FLIGHT))).count();

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
        if (!checkPath(oldPath) && checkPath(newPath)) return newPath;
        if (!checkPath(newPath) && checkPath(oldPath)) return oldPath;
        return oldPath.depth() > newPath.depth() ? newPath : oldPath;
    }

    private boolean checkPath(Path oldPath) {
        return oldPath.depth() == 0 || oldPath.path()[0].fromCity().equals(currentCity.city);
    }

    private Path combine(Path oldPath, Path newPath) {
        // we get here only if oldPath.depth is not 0 and we charter in newPath[0],
        // therefore we need to check if charter flight would give
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

    public List<MovingAction> lightestPath(City c) {
        GCity gc = findGCity(c);
        Path p = gc.lightestPathFromCurrentCity == null ? createShortestPath(gc) : gc.lightestPathFromCurrentCity;
        return Arrays.stream(p.path()).filter(Objects::nonNull).collect(Collectors.toList());
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path path1 = (Path) o;
            return Arrays.equals(path, path1.path);
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
