package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.City;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;


public class PathFinder {

    private IState state;
    public List<GCity> costGraph;
    public List<GCity> visitedCities;

    public PathFinder(IState state){
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));
    }



    public void evaluateCostGraph(){
        GCity currentCity = null;
        for (GCity gc : this.costGraph){
            if (gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity())) {
                currentCity = gc;
                break;
            }
        }

        if (currentCity != null) {
            visitedCities = new ArrayList<>();
            currentCity.nActionsWalking = 0;
            visitedCities.add(currentCity);
            int dist = 1;

            for (int i = 0; i < visitedCities.size(); i++) {
                currentCity = visitedCities.get(i);
                dist = currentCity.nActionsWalking < 4 ? currentCity.nActionsWalking+1 : MAX_VALUE;
                setNeighbourWalkingDistances(currentCity, dist);
            }

        }
    }

    private GCity findGCity(City c){
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }

    public void setNeighbourWalkingDistances(GCity currentCity, int dist){
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
        private boolean visited;
        public int nActionsWalking;
        public GCity(City city){
            this.city = city;
            this.nActionsWalking = -1;
            this.visited = false;
        }

    }
}
