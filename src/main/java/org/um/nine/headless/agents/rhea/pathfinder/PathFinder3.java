package org.um.nine.headless.agents.rhea.pathfinder;

import org.nd4j.common.util.ArrayUtil;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.rhea.state.StateEvaluation;
import org.um.nine.headless.agents.utils.IReportable;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.CityCard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.stream;
import static org.um.nine.headless.agents.rhea.pathfinder.PathFinder3.Path.fromList;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.abilityCure;
import static org.um.nine.headless.game.domain.ActionType.*;

public class PathFinder3 implements IReportable {

    protected final IState state;
    protected final ArrayList<GCity> costGraph;
    protected final Player currentPlayer;
    protected final GCity currentCity;
    private PathFinder3 lightPathsFinder;

    protected PathFinder3(IState state) {
        this(state, state.getPlayerRepository().getCurrentPlayer(), state.getPlayerRepository().getCurrentPlayer().getCity());
    }

    protected PathFinder3(IState state, Player currentPlayer) {
        this(state, currentPlayer, currentPlayer.getCity());
    }

    public PathFinder3(IState state, Player currentPlayer, City currentCity) {
        this.state = state;
        this.costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            this.costGraph.add(new GCity(c));
        this.currentPlayer = currentPlayer;
        this.currentCity = findGCity(currentCity);
    }

    protected void evaluatePaths() {
        this.evaluateLightPaths();
        this.evaluateExpensivePaths();
    }

    protected void evaluateLightPaths() {
        var walkingPathFinder = new WalkingPathFinder(
                this.state,
                this.currentPlayer,
                this.currentCity.city
        );

        var shuttleFlightPathFinder = new ShuttleFlightPathFinder(walkingPathFinder);

        for (GCity gc : this.costGraph) {
            gc.lightPath = findGCity(gc, shuttleFlightPathFinder.costGraph).lightPath;
        }

        this.lightPathsFinder = shuttleFlightPathFinder;
    }

    protected void evaluateExpensivePaths() {
        if (this.lightPathsFinder == null) throw new IllegalStateException();

        var directFlightPathFinder = new DirectFlightPathFinder(this.lightPathsFinder);
        for (GCity gc : this.costGraph) {
            GCity map = findGCity(gc, directFlightPathFinder.costGraph);
            gc.lightPath = map.lightPath;
            gc.expensivePath = map.expensivePath == null ? new Path(new MovingAction[0]) : map.expensivePath;
        }

        var charterFlightPathFinder = new CharterFlightPathFinder(directFlightPathFinder);

        for (GCity gc : this.costGraph) {
            gc.lightPath = findGCity(gc, charterFlightPathFinder.costGraph).lightPath;
            gc.expensivePath = findGCity(gc, charterFlightPathFinder.costGraph).expensivePath;
        }

    }

    protected GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }

    protected static GCity findGCity(GCity gc, List<GCity> costGraph) {
        return costGraph.stream().filter(gCity -> gCity.equals(gc)).findFirst().get();
    }

    protected List<MovingAction> getLightestPath(City toCity) {
        GCity gc = findGCity(toCity);
        if (gc.hasLightestPath())
            return stream(gc.lightPath.path).filter(Objects::nonNull).collect(Collectors.toList());
        return new ArrayList<>();
    }

    protected List<MovingAction> getExpensivePath(City toCity) {
        GCity gc = findGCity(toCity);
        if (gc.hasExpensivePath())
            return stream(gc.expensivePath.path).filter(Objects::nonNull).collect(Collectors.toList());
        return new ArrayList<>();
    }

    protected List<MovingAction> getShortestPath(City toCity, boolean needCardCity) {
        if (needCardCity) return getLightestPath(toCity);
        else {   // evaluate which one is better
            List<MovingAction> light = getLightestPath(toCity),
                    expensive = getExpensivePath(toCity);

            return expensive.size() == 0 ||              // if there is no expensive path return lightest
                    light.size() == expensive.size() ||              // if they have the same size or if they both empty return lightest
                    light.size() < expensive.size() ?               // finally, if lightest is shorter return lightest
                    light : expensive;
        }
    }

    protected void buildAllPaths() {
        for (GCity gc : this.costGraph) gc.lightPath = createPath(gc);
    }

    protected Path createPath(GCity gc) {
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

    protected String logGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append(currentPlayer.getName()).append(" ").append(currentPlayer.getRole().getName()).append("\n");
        sb.append("Cards in hand : ").append(state.getPlayerRepository().getCurrentPlayer().getHand().stream().map(Card::getName).collect(Collectors.toList())).append("\n");
        sb.append("============================================================================================\n");
        for (GCity gc : this.costGraph) {
            sb.append(String.format("%1$-20s", gc.city.getName())).append(gc.lightPath.toString()).append("\n");
            sb.append(String.format("%1$-20s", gc.city.getName())).append(gc.expensivePath.toString()).append("\n");
        }
        append("============================================================================================\n");
        return sb.toString();
    }

    protected boolean isDiscardableCard(CityCard cc, double ac, int i) {
        // we remove it to check if discarding wouldn't affect ability to cure disease
        currentPlayer.getHand().remove(cc);
        double ac_new = StateEvaluation.abilityCure2(state, cc.getCity().getColor());
        currentPlayer.getHand().add(i, cc);
        return ac_new == ac;
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

    protected Path findExpensivePath(GCity prevCityA, GCity previouslyEvaluatedCityB, GCity newlyEvaluatedCityB) {
        if (prevCityA.expensivePath == null)
            prevCityA.expensivePath = previouslyEvaluatedCityB.expensivePath == null ? new Path(new MovingAction[0]) : previouslyEvaluatedCityB.expensivePath;
        if (newlyEvaluatedCityB.hasLightestPath()) {
            int newDepth = prevCityA.dist + newlyEvaluatedCityB.dist;
            if (newDepth <= 4)
                return new Path(ArrayUtil.combine(
                        prevCityA.expensivePath.path,
                        newlyEvaluatedCityB.lightPath.path
                ));
        }
        return new Path(new MovingAction[0]);
    }

    protected Path compareAndFindExpensivePaths(GCity prevCityA, GCity previouslyEvaluatedExpensive, GCity postActionWalkingCity) {
        if (postActionWalkingCity.hasLightestPath()) {
            int newDepth = prevCityA.dist + postActionWalkingCity.dist;
            if (newDepth <= 4 && prevCityA.hasExpensivePath())
                if (!previouslyEvaluatedExpensive.hasExpensivePath() || newDepth < previouslyEvaluatedExpensive.expensivePath.depth())
                    return new Path(ArrayUtil.combine(
                            prevCityA.expensivePath.path,
                            postActionWalkingCity.lightPath.path
                    ));
                else return previouslyEvaluatedExpensive.expensivePath;
        }
        return previouslyEvaluatedExpensive.expensivePath;
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
                List<GCity> reachableWalkingRS = this.costGraph.
                        stream().
                        filter(gc -> gc.city.getResearchStation() != null).
                        map(gc -> findGCity(gc, beforeShuttle)).
                        filter(GCity::hasLightestPath).
                        collect(Collectors.toList());
                for (GCity gc : reachableWalkingRS) buildShuttlePathsFromCity(gc);
            }
        }

        public void buildShuttlePathsFromCity(GCity fromCity) {
            if (fromCity.dist == 4) return;
            List<GCity> shuttleToRSCities = this.costGraph.
                    stream().
                    filter(gc -> !gc.equals(fromCity)).
                    filter(gc -> gc.city.getResearchStation() != null).
                    collect(Collectors.toList());

            for (GCity shuttledToRS : shuttleToRSCities) {
                shuttledToRS.dist = fromCity.dist + 1;
                shuttledToRS.prev = fromCity;
                shuttledToRS.prevA = SHUTTLE;
                shuttledToRS.lightPath = this.createPath(shuttledToRS);
                WalkingPathFinder postShuttleGraph = new WalkingPathFinder(this.state, this.currentPlayer, shuttledToRS.city);
                for (GCity postShuttleGc : this.costGraph) {
                    GCity newlyEvaluatedCity = findGCity(postShuttleGc, postShuttleGraph.costGraph);
                    GCity previouslyEvaluatedCity = findGCity(postShuttleGc, this.beforeShuttle);
                    postShuttleGc.lightPath = this.compareAndFindLightPath(shuttledToRS, previouslyEvaluatedCity, newlyEvaluatedCity);
                }
            }
        }
    }

    protected static class WalkingPathFinder extends PathFinder3 {
        public WalkingPathFinder(IState state, Player player, City city) {
            super(state, player, city);
            this.evaluatePaths();
            this.buildAllPaths();
        }

        public WalkingPathFinder(IState state, Player player) {
            this(state, player, player.getCity());
        }

        public WalkingPathFinder(IState state) {
            this(state, state.getPlayerRepository().getCurrentPlayer(), state.getPlayerRepository().getCurrentPlayer().getCity());
        }

        protected void evaluatePaths() {
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

        protected List<MovingAction> getPath(City toCity) {
            GCity gc = findGCity(toCity);
            if (gc.lightPath == null || gc.lightPath.depth() == 0) return new ArrayList<>();
            return stream(gc.lightPath.path).filter(Objects::nonNull).collect(Collectors.toList());
        }

        protected String logGraph() {
            StringBuilder sb = new StringBuilder();
            sb.append(currentPlayer.getName()).append(" ").append(currentPlayer.getRole().getName()).append("\n");
            sb.append("Cards in hand : ").append(state.getPlayerRepository().getCurrentPlayer().getHand().stream().map(Card::getName).collect(Collectors.toList())).append("\n");
            sb.append("============================================================================================\n");
            for (GCity gc : this.costGraph) {
                if (gc.hasPath())
                    sb.append(String.format("%1$-20s", gc.city.getName())).append(gc.lightPath.toString()).append("\n");
            }
            append("============================================================================================\n");
            return sb.toString();
        }
    }

    protected static class DirectFlightPathFinder extends PathFinder3 {
        protected final List<GCity> beforeDirect;

        public DirectFlightPathFinder(PathFinder3 beforeDirect) {
            super(beforeDirect.state, beforeDirect.currentPlayer, beforeDirect.currentCity.city);
            this.beforeDirect = beforeDirect.costGraph;
            this.evaluatePaths();
        }

        protected void evaluatePaths() {
            List<GCity> directCities = IntStream.
                    range(0, this.currentPlayer.getHand().size()).   // go through all cards
                            filter(i -> {
                        CityCard cc = (CityCard) this.currentPlayer.getHand().get(i);
                        return this.isDiscardableCard(cc, abilityCure(state, cc.getCity().getColor()), i);
                        //check if it's discardable
                    }).
                    mapToObj(i -> ((CityCard) this.currentPlayer.getHand().get(i)).getCity()).
                    map(this::findGCity).
                    filter(gc -> !gc.equals(currentCity)).
                    map(gc -> findGCity(gc, beforeDirect)).
                    filter(gc -> !gc.hasLightestPath() || gc.lightPath.depth() > 3).
                    // we select cities which where not reachable before
                    // or if the path to get there was size 4 (otherwise just walk there)

//                  map(gc -> findGCity(gc , this.costGraph)).       // re-map to original graph city
        collect(Collectors.toList());


            if (directCities.isEmpty()) {
                this.costGraph.forEach(gc -> {
                    gc.lightPath = findGCity(gc, beforeDirect).lightPath;
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
                        GCity previouslyEvaluatedCity = findGCity(postDirectGc, this.beforeDirect);
                        postDirectGc.lightPath = previouslyEvaluatedCity.lightPath;
                        postDirectGc.expensivePath = this.findExpensivePath(directCity, previouslyEvaluatedCity, newlyEvaluatedCity);
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
            this.evaluatePaths();
        }

        protected void evaluatePaths() {
            GCity fromCharter = IntStream.
                    range(0, currentPlayer.getHand().size()).
                    filter(i -> {
                        CityCard cc = (CityCard) currentPlayer.getHand().get(i);
                        return isDiscardableCard(cc, abilityCure(state, cc.getCity().getColor()), i);
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
                        map(gc -> findGCity(gc, beforeCharter)).
                        filter(
                                gc -> !gc.equals(fromCharter) &&
                                        !gc.equals(currentCity) &&
                                        (!gc.hasExpensivePath() || gc.lightPath.depth() == 0 || gc.lightPath.depth() > 3)
                        ).
                        collect(Collectors.toList());

                for (GCity toCharterGc : toCharterGCities) {
                    int newDist = fromCharter.dist + 1;
                    GCity prevPath = findGCity(toCharterGc, beforeCharter);
                    if (prevPath.hasExpensivePath() && newDist > prevPath.expensivePath.depth()) continue;

                    prevPath.dist = newDist;
                    prevPath.prev = fromCharter;
                    prevPath.prevA = CHARTER_FLIGHT;
                    prevPath.expensivePath = createPath(prevPath);

                    WalkingPathFinder postDirectFlight = new WalkingPathFinder(this.state, this.currentPlayer, prevPath.city);

                    for (GCity postDirectGc : this.costGraph) {
                        GCity newlyEvaluatedCity = findGCity(postDirectGc, postDirectFlight.costGraph);
                        GCity previouslyEvaluatedCity = findGCity(postDirectGc, this.beforeCharter);
                        postDirectGc.lightPath = previouslyEvaluatedCity.lightPath;
                        postDirectGc.expensivePath = this.compareAndFindExpensivePaths(prevPath, previouslyEvaluatedCity, newlyEvaluatedCity);
                    }
                }
            } else {
                this.costGraph.forEach(gc -> {
                    gc.lightPath = findGCity(gc, beforeCharter).lightPath;
                    gc.expensivePath = findGCity(gc, beforeCharter).expensivePath;
                });
            }
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

    public static void main(String[] args) throws Exception {
        ExperimentalGame game = new ExperimentalGame();
        game.getCurrentState().getPlayerRepository().nextPlayer();
        Player p = game.getCurrentState().getPlayerRepository().getCurrentPlayer();
        game.getCurrentState().getPlayerRepository().setCurrentRoundState(RoundState.DRAW);
        game.getCurrentState().getPlayerRepository().playerAction(null, game.getCurrentState());
        game.getCurrentState().getPlayerRepository().setCurrentPlayer(p);
        game.getCurrentState().getPlayerRepository().drive(
                game.getCurrentState().getPlayerRepository().getCurrentPlayer(),
                game.getCurrentState().getCityRepository().getCities().get("Mexico City"),
                game.getCurrentState(),
                true
        );
        City cairo = game.getCurrentState().getCityRepository().getCities().get("Cairo");
        game.getCurrentState().getCityRepository().addResearchStation(cairo);
        PathFinder3 pf = new PathFinder3(game.getCurrentState());
        pf.evaluatePaths();
        System.out.println(pf.logGraph());
    }
}
