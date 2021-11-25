package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.EventCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;


public class PathFinder {

    private IState state;
    public List<GCity> costGraph;
    public List<GCity> visitedCities;

    public PathFinder(IState state) {
        this.state = state;
        costGraph = new ArrayList<>();
        for (City c : this.state.getCityRepository().getCities().values())
            costGraph.add(new GCity(c));
    }


    public void evaluateCostGraph() {
        GCity currentCity = null;
        for (GCity gc : this.costGraph) {
            if (gc.city.equals(this.state.getPlayerRepository().getCurrentPlayer().getCity())) {
                currentCity = gc;
                break;
            }
        }

        evaluateCostGraphWalking(currentCity, 0);
        evaluateCostGraphDirectFlight(currentCity);


    }



    private void evaluateCostGraphWalking(GCity currentCity, int dist) {
        if (currentCity != null) {
            visitedCities = new ArrayList<>();
            currentCity.nActionsWalking = dist;
            visitedCities.add(currentCity);
            dist++;

            for (int i = 0; i < visitedCities.size(); i++) {
                currentCity = visitedCities.get(i);
                dist = currentCity.nActionsWalking < 4 ? currentCity.nActionsWalking + 1 : MAX_VALUE;
                setNeighbourWalkingDistances(currentCity, dist);
            }
        }
    }

    private void evaluateCostGraphDirectFlight(GCity currentCity) {
        List<CityCard> citiesInHand = new ArrayList<CityCard>();
        for (PlayerCard pc:state.getPlayerRepository().getCurrentPlayer().getHand()) {
            if(pc instanceof CityCard) citiesInHand.add((CityCard) pc);
        }

        for (GCity gc : this.costGraph) {
            gc.setNActionsDirectFlight(new ArrayList<Integer>(Collections.nCopies(citiesInHand.size(), -1)));
        }

        for (int i = 0; i < citiesInHand.size(); i++) {
            //we fly to cityCard destination and start walking from there
            evaluateCostGraphPostDirectFlight(findGCity(citiesInHand.get(i).getCity()),1, i);
        }
    }

    private void evaluateCostGraphPostDirectFlight(GCity currentCity, int dist, int cityCardIndex) {
        if (currentCity != null) {
            visitedCities = new ArrayList<>();
            currentCity.nActionsDirectFlight.set(cityCardIndex, dist);
            visitedCities.add(currentCity);
            dist++;

            for (int i = 0; i < visitedCities.size(); i++) {
                currentCity = visitedCities.get(i);
                int currentDist = currentCity.nActionsDirectFlight.get(cityCardIndex);
                dist = currentDist < 4 ? currentDist+1 : MAX_VALUE;
                setNeighbourDirectFlightDistances(currentCity, dist, cityCardIndex);
            }
        }
    }

    private GCity findGCity(City c) {
        return this.costGraph.stream().filter(gCity -> gCity.city.equals(c)).findFirst().orElse(null);
    }

    public void setNeighbourWalkingDistances(GCity currentCity, int dist) {
        for (City c : currentCity.city.getNeighbors()) {
            GCity gc = findGCity(c);
            if (gc.nActionsWalking == -1) {
                gc.nActionsWalking = dist;
                visitedCities.add(gc);
            }
        }
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
        public List<Integer> nActionsDirectFlight; //different value depending on which city card you use

        public GCity(City city) {
            this.city = city;
            this.nActionsWalking = -1;
        }

        public void setNActionsDirectFlight(List<Integer> nActionsDirectFlight) {
            this.nActionsDirectFlight = nActionsDirectFlight;
        }
    }
}
