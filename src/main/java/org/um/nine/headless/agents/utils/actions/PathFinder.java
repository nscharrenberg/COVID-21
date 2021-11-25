package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.City;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;


public class PathFinder {

    private final IState state;
    public List<GCity> costGraph;
    public List<GCity> visitedCities;

    public PathFinder(IState state){
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));

    }


    public GCity getCurrentCity(){
        return this.costGraph.stream().filter(gc-> gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity())).findFirst().orElse(null);
    }

    public void evaluateWalkingCostGraph(GCity currentCity) {
        visitedCities = new ArrayList<>();
        currentCity.nActionsWalking = 0;
        visitedCities.add(currentCity);

        for (int i = 0; i< visitedCities.size(); i++) {
            currentCity = visitedCities.get(i);
            int dist = currentCity.nActionsWalking < 4 ? currentCity.nActionsWalking + 1 : MAX_VALUE;
            setNeighbourWalkingDistances(currentCity, dist);
        }
    }


    //Assume walking cost graph has been evaluated
    //Assume there is only a sin
    public void evaluateShuttleCostGraph() {
        GCity closestStation = this.costGraph.stream().
                filter(gc -> gc.city.getResearchStation() != null &&
                                gc.nActionsWalking < 4).
                //must have done 3 actions at most to get to the closest research station
                //shuttle will spend one action
                findFirst().orElse(null);

        if (closestStation!= null) {
            GCity shuttleStation = this.costGraph.stream().
                    filter(gc -> gc.city.getResearchStation() != null &&
                            ! gc.city.equals(closestStation.city)).
                    findFirst().orElse(null);

            if (shuttleStation != null) {
                visitedCities = new ArrayList<>();
                int dist = closestStation.nActionsWalking+1;
                shuttleStation.nActionsShuttle = dist;
                visitedCities.add(shuttleStation);
                List<GCity> neighbours = new ArrayList<>();
                dist++;
                while (dist<=4){
                    addNeighbours(neighbours);
                    setNeighbourShuttleDistance(neighbours,dist);
                    neighbours.clear();
                    dist = dist+1;
                }
            }
        }
    }

    private void addNeighbours(List<GCity> neighbours){
        for (GCity gc : visitedCities){
            neighbours.addAll(
                    gc.city.getNeighbors().
                            stream().
                            map(this::findGCity).
                            filter(gcp-> ! visitedCities.contains(gcp)).
                            collect(Collectors.toList())
            );
        }
    }

    private void setNeighbourShuttleDistance(List<GCity> neighbours, int dist){
        neighbours.forEach(gc -> {
            if (dist<=4) gc.nActionsShuttle = dist;
            visitedCities.add(gc);
        });
    }


    private GCity findGCity(City c){
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }


    private void setNeighbourWalkingDistances(GCity currentCity, int dist){
        for (City c : currentCity.city.getNeighbors()){
            GCity gc = findGCity(c);
            if (gc.nActionsWalking == -1){
                gc.nActionsWalking = dist;
                visitedCities.add(gc);
            }
        }
    }

    public static class GCity {
        public City city;
        public int nActionsWalking;
        public int nActionsShuttle;
        public GCity(City city){
            this.city = city;
            this.nActionsWalking = -1;
            this.nActionsShuttle = -1;
        }

    }
}
