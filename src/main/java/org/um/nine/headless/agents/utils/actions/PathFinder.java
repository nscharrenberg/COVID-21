package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;


public class PathFinder {

    private final IState state;
    public List<GCity> costGraph;
    public List<GCity> visitedCities;

    public PathFinder(IState state) {
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));

    }

    public void evaluateCostGraph(){
        this.evaluateCostGraphWalking();
        this.evaluateCostGraphShuttleFlight();
        this.evaluateCostGraphDirectFlight();
        this.evaluateCostGraphCharterFlight();
    }

    public GCity getCurrentCity(){
        return this.costGraph.stream().filter(gc-> gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity())).findFirst().orElse(null);
    }


    public void evaluateCostGraphWalking() {
        GCity currentCity = getCurrentCity();
        int dist = 0;
        if (currentCity != null) {
            visitedCities = new ArrayList<>();
            currentCity.nActionsWalking = dist;
            visitedCities.add(currentCity);
            for (int i = 0; i < visitedCities.size(); i++) {
                currentCity = visitedCities.get(i);
                dist = currentCity.nActionsWalking < 4 ? currentCity.nActionsWalking + 1 : MAX_VALUE;
                setNeighbourWalkingDistances(currentCity, dist);
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
            evaluateCostGraphPostDirectFlight(findGCity(citiesInHand.get(i).getCity()), 1, i);
        }
    }

    private void evaluateCostGraphPostDirectFlight(GCity city, int dist, int cityCardIndex) {
        if (city != null) {
            visitedCities = new ArrayList<>();
            city.nActionsDirectFlight.set(cityCardIndex, dist);
            visitedCities.add(city);

            for (int i = 0; i < visitedCities.size(); i++) {
                city = visitedCities.get(i);
                int currentDist = city.nActionsDirectFlight.get(cityCardIndex);
                dist = currentDist < 4 ? currentDist + 1 : MAX_VALUE;
                setNeighbourDirectFlightDistances(city, dist, cityCardIndex);
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
                gc.nActionsCharterFlight.set(i, city.nActionsWalking <= 3 ? city.nActionsWalking + 1 : MAX_VALUE);
            }
        }
    }

    private GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }


    private void setNeighbourWalkingDistances(GCity currentCity, int dist){
        for (City c : currentCity.city.getNeighbors()){
            GCity gc = findGCity(c);
            if (gc.nActionsWalking == -1) {
                gc.nActionsWalking = dist;
                visitedCities.add(gc);
            }
        }
    }

    //Assume walking cost graph has been evaluated
    //Assume we reach the closest station then move to another on the map
    public void evaluateCostGraphShuttleFlight() {
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

    public void setNeighbourDirectFlightDistances(GCity currentCity, int dist, int cityCardIndex) {
        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (gc.nActionsDirectFlight.get(cityCardIndex) == -1) {
                gc.nActionsDirectFlight.set(cityCardIndex, dist);
                visitedCities.add(gc);
            }
        }
    }



    public static class GCity {
        public City city;
        public int nActionsWalking;
        public int nActionsShuttle;
        public List<Integer> nActionsDirectFlight; //different value depending on which city card you use
        public List<Integer> nActionsCharterFlight; //different value depending on which city card you use

        public GCity(City city){
            this.city = city;
            this.nActionsWalking = -1;
            this.nActionsShuttle = -1;
        }

        public void setNActionsDirectFlight(List<Integer> nActionsDirectFlight) {
            this.nActionsDirectFlight = nActionsDirectFlight;
        }

        public void setNActionsCharterFlight(List<Integer> nActionsCharterFlight) {
            this.nActionsCharterFlight = nActionsCharterFlight;
        }
    }
}
