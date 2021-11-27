package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.MAX_VALUE;


public class PathFinder {

    private final IState state;
    private final List<GCity> costGraph;
    private List<GCity> visitedCities;

    public PathFinder(IState state) {
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));

    }
    public PathFinder evaluateCostGraph() {
        this.evaluateCostGraphWalking();
        this.evaluateCostGraphShuttleFlight();
        this.evaluateCostGraphDirectFlight();
        this.evaluateCostGraphCharterFlight();
        return this;
    }


    public void evaluateCostGraphWalking() {
        GCity currentCity = getCurrentCity();
        int dist = 0;
        if (currentCity != null) {
            visitedCities = new ArrayList<>();
            currentCity.walkingActionDepth = dist;
            visitedCities.add(currentCity);
            for (int i = 0; i < visitedCities.size(); i++) {
                currentCity = visitedCities.get(i);
                dist = currentCity.walkingActionDepth < 4 ? currentCity.walkingActionDepth + 1 : MAX_VALUE;
                setNeighbourWalkingDistances(currentCity, dist);
            }
        }
    }
    //Assume walking cost graph has been evaluated
    public void evaluateCostGraphCharterFlight() {
        List<CityCard> citiesInHand = new ArrayList<>();
        for (PlayerCard pc : state.getPlayerRepository().getCurrentPlayer().getHand()) {
            if (pc instanceof CityCard) citiesInHand.add((CityCard) pc);
        }

        for (GCity gc : costGraph) {
            gc.setNActionsCharterFlight(new ArrayList<>(Collections.nCopies(citiesInHand.size(), -1)));
        }

        for (int i = 0; i < citiesInHand.size(); i++) {
            GCity city = findGCity(citiesInHand.get(i).getCity());

            for (GCity gc : costGraph) {
                gc.charterFlightActionsDepthList.set(i, city.walkingActionDepth <= 3 ? city.walkingActionDepth + 1 : MAX_VALUE);
            }
        }
    }
    public void evaluateCostGraphDirectFlight() {
        List<CityCard> citiesInHand = new ArrayList<>();
        for (PlayerCard pc : state.getPlayerRepository().getCurrentPlayer().getHand()) {
            if (pc instanceof CityCard) citiesInHand.add((CityCard) pc);
        }

        for (GCity gc : this.costGraph) {
            gc.setNActionsDirectFlight(new ArrayList<>(Collections.nCopies(citiesInHand.size(), -1)));
        }

        for (int i = 0; i < citiesInHand.size(); i++) {
            //we fly to cityCard destination and start walking from there
            evaluateCostGraphPostDirectFlight(findGCity(citiesInHand.get(i).getCity()),  i);
        }
    }
    private void evaluateCostGraphPostDirectFlight(GCity city, int cityCardIndex) {
        if (city != null) {
            visitedCities = new ArrayList<>();
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
    //Assume walking cost graph has been evaluated
    //Assume we reach the closest station then move to another on the map
    public void evaluateCostGraphShuttleFlight() {
        GCity closestStation = this.costGraph.stream().
                filter(gc -> gc.city.getResearchStation() != null &&
                        gc.walkingActionDepth < 4).

                        findFirst().orElse(null);

        //must have done 3 actions at most to get to the closest research station
        //shuttle will spend one action

        if (closestStation!= null) {
            GCity shuttleCity = this.costGraph.stream().
                    filter(gc -> gc.city.getResearchStation() != null &&
                            ! gc.city.equals(closestStation.city)).findFirst().orElse(null);

            if (shuttleCity != null) {
                shuttleCity.shuttle = new MovingAction(ActionType.SHUTTLE, closestStation.city, shuttleCity.city);
                evaluateCostGraphPostShuttleFlight(shuttleCity,closestStation.walkingActionDepth+1);
            }

        }
    }


    private void evaluateCostGraphPostShuttleFlight(GCity city, int dist) {
        visitedCities = new ArrayList<>();
        city.shuttleActionDepth = dist;
        visitedCities.add(city);

        for (int i = 0; i < visitedCities.size(); i++) {
            city = visitedCities.get(i);
            int currentDist = city.shuttleActionDepth;
            dist = currentDist < 4 ? currentDist + 1 : MAX_VALUE;
            setNeighbourPostShuttleDistance(city, dist);
        }

    }





    public GCity getCurrentCity() {
        return this.costGraph.stream().filter(gc-> gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity())).findFirst().orElse(null);
    }
    public GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }
    public List<GCity> getCostGraph() {
        return costGraph;
    }
    private void setNeighbourWalkingDistances(GCity currentCity, int dist) {
        for (City c : currentCity.city.getNeighbors()){
            GCity gc = findGCity(c);
            if (gc.walkingActionDepth == -1) {
                gc.walkingActionDepth = dist;
                gc.walking = new MovingAction(ActionType.DRIVE, currentCity.city, gc.city);
                visitedCities.add(gc);
            }
        }
    }
    private void setNeighbourPostShuttleDistance(GCity currentCity, int dist) {
        if (currentCity.city.getName().equals("Tokyo")){

        }
        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (gc.shuttleActionDepth == -1) {
                gc.shuttleActionDepth = dist;
                gc.walkingPostShuttle = new MovingAction(ActionType.DRIVE,currentCity.city, gc.city);
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


    /**
     * Everything on this instance is based on the current city given in the state at the initialization
     */
    public static class Descriptor extends PathFinder {

        public Descriptor(IState state) {
            super(state);
            this.evaluateCostGraph();
        }

        public Descriptor evaluateCostGraph() {
            super.evaluateCostGraph();

            return this;
        }

        /**
         * builds the path to get to the current city by walking
         * @param gc the city to be evaluated (will be the last step of the path
         * @return A Tuple with key the path build so far and value the last city visited (first city in the path)
         */
        public String buildWalkPathString(GCity gc, int walkingActionDepth) {
            StringBuilder walkPath = null;
            if (gc.walking!= null&& (walkingActionDepth<=4) && (walkingActionDepth>0)){
                walkPath = new StringBuilder();
                for (int i = 0; i <  walkingActionDepth; i++){
                    walkPath.append("[")
                            .append(gc.city.getName())
                            .append(" <-w- ")
                            .append(gc.walking.fromCity().getName())
                            .append("]");
                    gc = findGCity(gc.walking.fromCity());
                }
            }
            return walkPath == null ? "" : walkPath.toString();
        }
        public List<MovingAction> buildWalkPathList(GCity gc, int walkingActionDepth) {
            List<MovingAction> actions = new ArrayList<>();
            if (gc.walking!= null&& (walkingActionDepth<=4) && (walkingActionDepth>0)){
                for (int i = 0; i <  walkingActionDepth; i++){
                    actions.add(new MovingAction(ActionType.DRIVE,gc.walking.fromCity(),gc.city));
                    gc = findGCity(gc.walking.fromCity());
                }
            }
            return actions;
        }
        /**
         * builds the path to get to the current city by walking after shuttle flight
         * @param gc the city to be evaluated , will be the last step of the path
         * @return A Tuple with key the path build so far and value the last city visited (first city in the path)
         */
        public Map.Entry<String, GCity> buildWalkPathPostShuttleString(GCity gc, int walkingActionDepth) {
            StringBuilder walkPath = null;
            if (gc.walkingPostShuttle!= null){
                walkPath = new StringBuilder();
                for (int i = 0; i <  walkingActionDepth; i++){
                    if (gc.shuttle!= null) break;
                    walkPath.append("[")
                            .append(gc.city.getName())
                            .append(" <-w- ")
                            .append(gc.walkingPostShuttle.fromCity().getName())
                            .append("]");
                    gc = findGCity(gc.walkingPostShuttle.fromCity());
                }
            }
            return Map.entry(walkPath == null ? "" : walkPath.toString(), gc);
        }
        public Map.Entry<List<MovingAction>, GCity> buildWalkPathPostShuttleList(GCity gc, int walkingActionDepth) {
            List<MovingAction> actions = new ArrayList<>();
            if (gc.walkingPostShuttle != null) {
                for (int i = 0; i< walkingActionDepth; i++){
                    if (gc.shuttle!= null) break;
                    actions.add(new MovingAction(ActionType.DRIVE, gc.walkingPostShuttle.fromCity(), gc.city));
                    gc = findGCity(gc.walkingPostShuttle.fromCity());
                }
            }
            return Map.entry(actions, gc);
        }
        /**
         * String description of the shuttle path from current city to shuttle and afterwards
         * @param gc the city to be evaluated , will be the last step of the path
         * @return a String description of the path
         */
        public String buildShuttlePathString(GCity gc) {
            String shuttlePath = null;
            if (gc.shuttleActionDepth>0 && gc.shuttleActionDepth<=3){
                Map.Entry<String, GCity> path = buildWalkPathPostShuttleString(gc, gc.shuttleActionDepth-1);
                shuttlePath = path.getKey();
                gc = path.getValue();
                shuttlePath +="[" + gc.city.getName()+ " <-s- " + gc.shuttle.fromCity().getName() + "]";
                gc =findGCity(gc.shuttle.fromCity());
                shuttlePath += buildWalkPathString(gc, gc.walkingActionDepth);
            }
            return shuttlePath == null? "" : shuttlePath;
        }
        public List<MovingAction> buildShuttlePathList(GCity gc) {
            List<MovingAction> shuttlePath = new ArrayList<>();
            if (gc.shuttleActionDepth>0 && gc.shuttleActionDepth<=4){
                Map.Entry<List<MovingAction>, GCity> path = buildWalkPathPostShuttleList(gc, gc.shuttleActionDepth-1);
                shuttlePath = path.getKey();
                gc = path.getValue();
                shuttlePath.add(new MovingAction(ActionType.SHUTTLE, gc.shuttle.fromCity(), gc.city));
                gc =findGCity(gc.shuttle.fromCity());
                shuttlePath.addAll(buildWalkPathList(gc, gc.walkingActionDepth));
            }
            return shuttlePath;
        }
        /**
         * Info string description per city
         * @param city the city to be described
         * @return walking path and shuttling path combined
         */
        public String getCityInfo(City city) {
            GCity gc = findGCity(city);
            // walking path   <-w- is walking arrow
            String walkPath = buildWalkPathString(gc, gc.walkingActionDepth);
            //shuttle path  <-s- is shuttle arrow
            String shuttlePath = buildShuttlePathString(gc);
            return "{ " + gc.city.getName() + "\n\twalk path: " + walkPath + "\n\tshuttle path: " + shuttlePath + " \n}";
        }

        public List<MovingAction> shortestPath(City toCity) {
            GCity dest = findGCity(toCity);
            List<MovingAction> walking = new ArrayList<>(), shuttling = new ArrayList<>();
            if (dest.shortestWalkingPath == null) walking = dest.shortestWalkingPath = buildWalkPathList(dest, dest.walkingActionDepth);
            if (dest.shortestShuttlingPath == null) shuttling = dest.shortestShuttlingPath =  buildShuttlePathList(dest);
            if (walking.isEmpty() && shuttling.isEmpty()) return new ArrayList<>();
            if (walking.isEmpty()) return shuttling;
            if (shuttling.isEmpty()) return walking;
            return shuttling.size() < walking.size()? shuttling:walking;
        }
    }




    public static class GCity {
        public City city;
        public MovingAction walking, shuttle, walkingPostShuttle;
        public int walkingActionDepth, shuttleActionDepth;
        public List<Integer> directFlightActionsDepthList, charterFlightActionsDepthList; //different value depending on which city card you use

        public List<MovingAction> shortestWalkingPath, shortestShuttlingPath;

        public GCity(City city) {
            this.city = city;
            this.walkingActionDepth = -1;
            this.shuttleActionDepth = -1;
        }

        private void setNActionsDirectFlight(List<Integer> nActionsDirectFlight) {
            this.directFlightActionsDepthList = nActionsDirectFlight;
        }

        private void setNActionsCharterFlight(List<Integer> nActionsCharterFlight) {
            this.charterFlightActionsDepthList = nActionsCharterFlight;
        }
    }

}
