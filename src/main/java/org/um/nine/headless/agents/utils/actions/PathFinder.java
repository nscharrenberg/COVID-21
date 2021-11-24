package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.City;

import java.util.ArrayList;
import java.util.List;


public class PathFinder {

    private IState state;
    public List<GCity> costGraph;

    public PathFinder(IState state){
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));
    }



    public void evaluateCostGraph(){
        GCity currentCity = null;
        for (GCity gc : this.costGraph){
            if (gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity()))
                currentCity = gc;
        }
        int dist = 0;
        if (currentCity != null) findWalkingDistanceFromCity(currentCity, dist);

    }

    private GCity findGCity(City c){
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }


    public void findWalkingDistanceFromCity(GCity currentCity, int dist){

        if (dist == 0) {
            currentCity.nActionsWalking = dist;
        }
        currentCity.visited = true;
        for (City c : currentCity.city.getNeighbors()){
            GCity gc = findGCity(c);
            if (!gc.visited){
                if (dist+1 <= 4) gc.nActionsWalking = dist+1;
            }
        }


        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (!gc.visited) findWalkingDistanceFromCity(gc, dist+1);
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
