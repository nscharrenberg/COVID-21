package org.um.nine.headless.agents.rhea.pathfinder;

import org.apache.commons.lang3.ArrayUtils;
import org.nd4j.common.util.ArrayUtil;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.rhea.state.StateEvaluation;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.stream;
import static org.um.nine.headless.agents.rhea.pathfinder.PathFinder3.Path.fromList;
import static org.um.nine.headless.game.domain.ActionType.*;

public class PathFinder3 implements IReportable {

    protected final IState state;
    protected final ArrayList<GCity> costGraph;
    protected final Player currentPlayer;
    protected final GCity currentCity;
    private PathFinder3 lightPathsFinder;
    public PathFinder3(IState state, Player currentPlayer, City currentCity) {
        this.state = state;
        this.costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            this.costGraph.add(new GCity(c));
        this.currentPlayer = currentPlayer;
        this.currentCity = findGCity(currentCity);
    }

    public void evaluatePaths() {
        this.evaluateLightPaths();
        this.evaluateExpensivePaths();
        //this.logGraph();
    }

    protected void evaluateLightPaths() {
        var walkingPathFinder = new WalkingPathFinder(this.state, this.currentPlayer, this.currentCity.city);
        var shuttleFlightPathFinder = new ShuttleFlightPathFinder(walkingPathFinder);
        this.costGraph.forEach(gc -> gc.lightPath = findGCity(gc, shuttleFlightPathFinder.costGraph).lightPath);
        this.lightPathsFinder = shuttleFlightPathFinder;
    }

    protected void evaluateExpensivePaths() {
        if (this.lightPathsFinder == null) throw new IllegalStateException();
        var directFlightPathFinder = new DirectFlightPathFinder(this.lightPathsFinder);
        var charterFlightPathFinder = new CharterFlightPathFinder(directFlightPathFinder);
        this.costGraph.forEach(gc -> gc.expensivePath = findGCity(gc, charterFlightPathFinder.costGraph).expensivePath);
    }

    protected GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }

    protected static GCity findGCity(GCity gc, List<GCity> costGraph) {
        return costGraph.stream().filter(gCity -> gCity.equals(gc)).findFirst().get();
    }

    public List<MovingAction> lightestPath(City toCity) {
        GCity gc = findGCity(toCity);
        if (gc.hasLightestPath()) return new ArrayList<>(List.of(gc.lightPath.path));
        return new ArrayList<>();
    }

    public List<MovingAction> shortestPath(City toCity) {
        GCity gc = findGCity(toCity);
        if (gc.hasExpensivePath()) return new ArrayList<>(List.of(gc.expensivePath.path));
        return new ArrayList<>();
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


    public List<MovingAction> getPath(City toCity, boolean needCardCity) {
        List<MovingAction> light = lightestPath(toCity),
                expensive = shortestPath(toCity);
        if (needCardCity && isSpendingCard(expensive, toCity)) return light;
        else {   // evaluate which one is better
            return expensive.size() == 0 ||              // if there is no expensive path return lightest
                    light.size() == expensive.size() ||              // if they have the same size or if they both empty return lightest
                    light.size() < expensive.size() ?               // finally, if lightest is shorter return lightest
                    light : expensive;
        }
    }

    protected static Path createPath(GCity gc, GCity currentCity) {
        if (gc.city.equals(currentCity.city)) return new Path(new MovingAction[0]);
        GCity current = gc;
        List<MovingAction> movingActions = new ArrayList<>();
        int i = 0;
        while (i < 4 && current.prev != null && !current.city.equals(currentCity.city)) {
            movingActions.add(new MovingAction(current.prevA, current.prev.city, current.city));
            current = current.prev;
            i++;
        }
        if (!current.equals(currentCity)) return new Path(new MovingAction[0]);
        Collections.reverse(movingActions);
        return fromList(movingActions);
    }

    protected static Path createPath(GCity gc, Path previousPath) {
        MovingAction[] newPath = ArrayUtils.add(previousPath.path(), new MovingAction(gc.prevA, gc.prev.city, gc.city));
        return new Path(newPath);
    }

    protected List<String> logGraph() {
        List<String> log = new ArrayList<>();
        log.add(currentPlayer.getName() + (" ") + currentPlayer.getRole().getName());
        log.add("Cards in hand : " + state.getPlayerRepository().getCurrentPlayer().getHand().stream().map(Card::getName).collect(Collectors.toList()));
        log.add("Diseases around : ");
        log.add(this.logDiseasesByCityAndColor(this.state).toString());
        log.add("============================================================================================");
        for (GCity gc : this.costGraph) {
            String cubes = gc.city.getCubes().stream().collect(
                            Collectors.groupingBy(Disease::getColor)).
                    entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue().size()).
                    collect(Collectors.toList()).toString();

            log.add(
                    String.format("%1$-20s", gc.city.getName()) +
                            String.format("%1$30s", cubes + "\t") +
                            String.format("%1$-4s", gc.city.getResearchStation() == null ? "" : "RS") +
                            String.format("%1$-150s", lightestPath(gc.city).toString()) +
                            String.format("%1$-20s", gc.city.getName()) +
                            String.format("%1$-150s", shortestPath(gc.city).toString())
            );
        }

        log.add("============================================================================================");
        // if (LOG) log.forEach(this::append);
        return log;
    }

    public boolean isDiscardableCard(CityCard cc, int i) {
        // we remove it to check if discarding wouldn't affect the best ability to cure disease
        double[] acAll = evaluateAbilityCureOfCards();
        currentPlayer.getHand().remove(cc);
        if (currentPlayer.getHand().isEmpty()) return false;
        double[] acNew = evaluateAbilityCureOfCards();
        currentPlayer.getHand().add(i, cc);
        return stream(acAll).max().getAsDouble() == stream(acNew).max().getAsDouble();
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

    protected Path compareAndFindLightPath(GCity prevCityA, GCity previouslyEvaluatedCityB, GCity newlyEvaluatedCityB) {
        if (newlyEvaluatedCityB.equals(prevCityA)) {
            if (prevCityA.lightPath == null) prevCityA.lightPath = previouslyEvaluatedCityB.lightPath;
            return prevCityA.lightPath;  // return new current city
        }

        if (newlyEvaluatedCityB.hasLightestPath()) {
            int newDepth = prevCityA.dist + newlyEvaluatedCityB.dist;
            if (newDepth <= 4 && newDepth < previouslyEvaluatedCityB.dist)
                return new Path(ArrayUtil.combine(
                        prevCityA.lightPath.path,
                        newlyEvaluatedCityB.lightPath.path
                ));
        }
        return previouslyEvaluatedCityB.lightPath;
    }


    protected Path findExpensivePath(GCity previousCity, GCity previouslyEvaluatedCityB, GCity newlyEvaluatedCityB) {
        if (newlyEvaluatedCityB.equals(currentCity)) return new Path(new MovingAction[0]);  // the actual current city
        if (previousCity.hasExpensivePath()) {
            if (newlyEvaluatedCityB.equals(previousCity) && previouslyEvaluatedCityB.equals(previousCity))
                return previousCity.expensivePath;         // this will be the city we direct flight to, so we return
            if (newlyEvaluatedCityB.hasLightestPath()) {                    // we walked from the post direct city, we want to see if the expensive path is better than the one previously evaluated
                int newDepth = previousCity.dist + newlyEvaluatedCityB.dist;
                if (newDepth <= 4 && newDepth > 0) {
                    if (previouslyEvaluatedCityB.hasExpensivePath() && newDepth >= previouslyEvaluatedCityB.expensivePath.depth()) {
                        return previouslyEvaluatedCityB.expensivePath;
                    }
                    return new Path(ArrayUtil.combine(previousCity.expensivePath.path, newlyEvaluatedCityB.lightPath.path));
                }
                if (previouslyEvaluatedCityB.hasExpensivePath()) return previouslyEvaluatedCityB.expensivePath;
            }
        }
        return new Path(new MovingAction[0]);
    }


    public static class WalkingPathFinder extends PathFinder3 {
        public WalkingPathFinder(IState state, Player player, City city) {
            super(state, player, city);
            this.evaluatePaths();
            this.buildAllPaths();
        }

        protected void buildAllPaths() {
            for (GCity gc : this.costGraph) gc.lightPath = createPath(gc, this.currentCity);
        }

        public void evaluatePaths() {
            this.currentCity.dist = 0;
            Comparator<GCity> comparator = Comparator.comparingInt(c -> c.city.getCubes().size());  // ;)
            PriorityQueue<GCity> Q = new PriorityQueue<>(comparator.reversed());
            for (GCity gc : this.costGraph)
                if (gc != currentCity) {
                    gc.prev = null;
                    gc.dist = MAX_VALUE;
                }
            GCity u;
            Q.add(currentCity);
            while (!Q.isEmpty()) {
                u = Q.poll();
                for (City c : u.city.getNeighbors()) {
                    GCity v = findGCity(c);
                    int d = u.dist + 1;
                    if (v.dist > d) {
                        Q.remove(v);
                        v.dist = d;
                        v.prev = u;
                        v.prevA = DRIVE;
                        Q.add(v);
                    }
                }
            }
        }

        public List<MovingAction> getPath(City toCity) {
            GCity gc = findGCity(toCity);
            if (gc.lightPath == null || gc.lightPath.depth() == 0) return new ArrayList<>();
            return stream(gc.lightPath.path).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    protected static class ShuttleFlightPathFinder extends PathFinder3 {
        private final List<GCity> beforeShuttle;

        public ShuttleFlightPathFinder(WalkingPathFinder walkingPathFinder) {
            super(walkingPathFinder.state, walkingPathFinder.currentPlayer, walkingPathFinder.currentCity.city);
            this.beforeShuttle = walkingPathFinder.costGraph;
            this.evaluatePaths();
        }

        public void evaluatePaths() {
            if (this.currentCity.city.getResearchStation() != null) {
                this.buildShuttlePathsFromCity(this.currentCity);
            } else {
                GCity reachableWalkingRS = this.costGraph.
                        stream().
                        filter(gc -> gc.city.getResearchStation() != null).
                        map(gc -> findGCity(gc, beforeShuttle)).
                        filter(GCity::hasLightestPath).
                        min(Comparator.comparingInt(gc -> gc.lightPath.depth())).
                        orElse(null);

                buildShuttlePathsFromCity(reachableWalkingRS);
            }
        }

        public void buildShuttlePathsFromCity(GCity fromCity) {
            List<GCity> shuttleToRSCities = this.costGraph.
                    stream().
                    filter(gc -> !gc.equals(fromCity)).
                    filter(gc -> gc.city.getResearchStation() != null).
                    collect(Collectors.toList());

            if (fromCity == null || fromCity.dist == 4 || shuttleToRSCities.isEmpty()) {
                this.costGraph.forEach(gc -> gc.lightPath = findGCity(gc, this.beforeShuttle).lightPath);
            } else {
                for (GCity shuttledToRS : shuttleToRSCities) {
                    shuttledToRS.dist = fromCity.dist + 1;
                    shuttledToRS.prev = fromCity;
                    shuttledToRS.prevA = SHUTTLE;
                    shuttledToRS.lightPath = createPath(shuttledToRS, this.currentCity);
                    WalkingPathFinder postShuttleGraph = new WalkingPathFinder(this.state, this.currentPlayer, shuttledToRS.city);
                    for (GCity postShuttleGc : this.costGraph) {
                        GCity newlyEvaluatedCity = findGCity(postShuttleGc, postShuttleGraph.costGraph);
                        GCity previouslyEvaluatedCity = findGCity(postShuttleGc, this.beforeShuttle);
                        postShuttleGc.lightPath = this.compareAndFindLightPath(shuttledToRS, previouslyEvaluatedCity, newlyEvaluatedCity);
                    }
                }
            }

        }
    }

    protected static class DirectFlightPathFinder extends PathFinder3 {
        protected final List<GCity> beforeDirect;

        public DirectFlightPathFinder(PathFinder3 beforeDirect) {
            super(beforeDirect.state, beforeDirect.currentPlayer, beforeDirect.currentCity.city);
            this.beforeDirect = beforeDirect.costGraph;
            this.costGraph.forEach(gc -> gc.lightPath = findGCity(gc, this.beforeDirect).lightPath);
            this.evaluatePaths();
        }

        public void evaluatePaths() {
            List<GCity> directCities = IntStream.
                    range(0, this.currentPlayer.getHand().size()).   // go through all cards
                            filter(i -> {
                        CityCard cc = (CityCard) this.currentPlayer.getHand().get(i);
                        return this.isDiscardableCard(cc, i);
                        //check if it's discardable
                    }).
                    mapToObj(i -> ((CityCard) this.currentPlayer.getHand().get(i)).getCity()).
                    map(this::findGCity).
                    filter(gc -> !gc.equals(currentCity)).
                    map(gc -> findGCity(gc, beforeDirect)).
//                    filter(gc -> !gc.hasLightestPath() || gc.lightPath.depth() > 3).
                    // we select cities which where not reachable before
                    // or if the path to get there was size 4 (otherwise just walk there)

//                  map(gc -> findGCity(gc , this.costGraph)).       // re-map to original graph city
        collect(Collectors.toList());

            if (directCities.isEmpty()) {

                this.costGraph.forEach(gc -> {
                    gc.expensivePath = new Path(new MovingAction[0]);
                });

            } else {
                for (GCity directCity : directCities) {
                    directCity.dist = currentCity.dist + 1;
                    directCity.prev = currentCity;
                    directCity.prevA = DIRECT_FLIGHT;
                    directCity.expensivePath = new Path(new MovingAction[]{new MovingAction(DIRECT_FLIGHT, currentCity.city, directCity.city)});
                    WalkingPathFinder postDirectFlight = new WalkingPathFinder(this.state, this.currentPlayer, directCity.city);

                    for (GCity postDirectGc : this.costGraph) {
                        GCity newlyEvaluatedCity = findGCity(postDirectGc, postDirectFlight.costGraph);
                        postDirectGc.expensivePath = this.findExpensivePath(directCity, postDirectGc, newlyEvaluatedCity);
                    }
                }
            }

        }
    }
    protected static class CharterFlightPathFinder extends PathFinder3 {
        protected final List<GCity> beforeCharter;

        public CharterFlightPathFinder(PathFinder3 beforeCharter) {
            super(beforeCharter.state, beforeCharter.currentPlayer, beforeCharter.currentCity.city);
            this.beforeCharter = beforeCharter.costGraph;
            this.costGraph.forEach(gc -> {
                gc.lightPath = findGCity(gc, this.beforeCharter).lightPath;
                gc.expensivePath = findGCity(gc, this.beforeCharter).expensivePath;
            });
            this.evaluatePaths();
        }

        public void evaluatePaths() {
            GCity fromCharter = IntStream.
                    range(0, currentPlayer.getHand().size()).
                    filter(i -> {
                        CityCard cc = (CityCard) currentPlayer.getHand().get(i);
                        return isDiscardableCard(cc, i);
                    }).
                    mapToObj(i -> ((CityCard) currentPlayer.getHand().get(i)).getCity()).
                    map(this::findGCity).
                    map(gc -> findGCity(gc, this.beforeCharter)).
                    filter(gc -> gc.equals(currentCity) || (gc.hasLightestPath() && gc.lightPath.depth() < 4)).
                    min(Comparator.comparingInt(gc -> gc.lightPath.path.length)).
                    orElse(null);


            if (fromCharter != null) {
                List<GCity> toCharterGCities = this.costGraph.
                        stream().
                        filter(gc -> !gc.equals(fromCharter) &&
                                !gc.equals(currentCity) &&
                                (!gc.hasLightestPath() || gc.lightPath.depth() > 3 || !gc.hasExpensivePath())
                        ).
                        collect(Collectors.toList());

                for (GCity toCharterGc : toCharterGCities) {
                    toCharterGc.prev = fromCharter;
                    toCharterGc.prevA = CHARTER_FLIGHT;
                    Path newPath = createPath(toCharterGc, fromCharter.lightPath);
                    toCharterGc.expensivePath = shorter(toCharterGc.expensivePath, newPath);
                    if (newPath.depth() == 4) continue;
                    WalkingPathFinder postCharterFlight = new WalkingPathFinder(this.state, this.currentPlayer, toCharterGc.city);
                    for (GCity postCharterGc : this.costGraph) {
                        GCity newlyEvaluatedCity = findGCity(postCharterGc, postCharterFlight.costGraph);
                        postCharterGc.expensivePath = this.findExpensivePath(toCharterGc, postCharterGc, newlyEvaluatedCity);
                    }
                }
            } else this.costGraph.forEach(gc -> gc.expensivePath = findGCity(gc, beforeCharter).expensivePath);
        }

        private Path shorter(Path p1, Path p2) {
            if (p1.depth() > 0 && p2.depth() == 0) return p1;
            if (p2.depth() > 0 && p1.depth() == 0) return p2;
            if (p1.depth() == p2.depth()) return p2;
            return p1.depth() > p2.depth() ? p2 : p1;
        }
    }

    protected static class GCity {
        private final City city;
        private ActionType prevA;
        private int dist;
        private GCity prev;
        private Path lightPath;
        private Path expensivePath;

        public GCity(City city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return city.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GCity gCity = (GCity) o;
            return Objects.equals(city, gCity.city);
        }

        public boolean hasPath() {
            return (lightPath != null && lightPath.depth() > 0) ||
                    (expensivePath != null && expensivePath.depth() > 0);
        }

        public boolean hasLightestPath() {
            return lightPath != null && lightPath.depth() > 0;
        }

        public boolean hasExpensivePath() {
            return expensivePath != null && expensivePath.depth() > 0;
        }
    }
    protected static record Path(MovingAction[] path) {
        public static Path fromList(List<MovingAction> list) {
            return new Path(list.toArray(ActionType.MovingAction[]::new));
        }

        int depth() {
            return (int) stream(path).filter(Objects::nonNull).count();
        }

        @Override
        public MovingAction[] path() {
            return stream(path).filter(Objects::nonNull).toArray(MovingAction[]::new);
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

}
