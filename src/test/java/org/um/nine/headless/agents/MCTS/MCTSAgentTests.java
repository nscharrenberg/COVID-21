package org.um.nine.headless.agents.MCTS;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.mcts.Actions;
import org.um.nine.headless.agents.mcts.MCTS;
import org.um.nine.headless.agents.rhea.experiments.ExperimentalGame;
import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.exceptions.InvalidMoveException;
import org.um.nine.headless.game.exceptions.MoveNotPossibleException;
import org.um.nine.headless.game.exceptions.PlayerLimitException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MCTSAgentTests {

    @Test
    public void expansionTest(){
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        MCTS mcts = new MCTS(state,1);
        mcts.expand(mcts.getRoot());
        mcts.getRoot().getChildren().forEach(c -> {
            System.out.println(c.getActions().toString());
        });
        assert(mcts.getRoot().getChildren().size() >= 1);
    }

    @Test
    public void simulationTest(){
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        MCTS mcts = new MCTS(state,1);

        try{
            IState newState = mcts.simulate(Actions.DRIVE,state);
            assert(!(newState.getPlayerRepository().getCurrentPlayer().getCity()).equals(newState.getCityRepository().getCities().get("Atlanta")));
        } catch (MoveNotPossibleException e) {
            System.out.println("Drive");
            e.printStackTrace();
        }

        try{
            City city = state.getPlayerRepository().getCurrentPlayer().getCity();
            state.getPlayerRepository().getCurrentPlayer().addHand(new CityCard(city));
            IState newState = mcts.simulate(Actions.CHARTER_FLIGHT,state);
            assert(!(newState.getPlayerRepository().getCurrentPlayer().getCity()).equals(city));
        } catch (MoveNotPossibleException e) {
            System.out.println("Charter");
            e.printStackTrace();
        }

        try{
            City city = ((CityCard) state.getPlayerRepository().getCurrentPlayer().getHand().stream().filter(c -> c instanceof CityCard).findFirst().orElse(null)).getCity();
            state.getPlayerRepository().getCurrentPlayer().addHand(new CityCard(city));
            IState newState = mcts.simulate(Actions.DIRECT_FLIGHT,state);
            Assertions.assertEquals(city,newState.getPlayerRepository().getCurrentPlayer().getCity());
        } catch (MoveNotPossibleException | NullPointerException e ) {
            System.out.println("Direct");
            e.printStackTrace();
        }

        try{
            City city = state.getCityRepository().getCities().get("Madrid");
            state.getCityRepository().addResearchStation(city);
            IState newState = mcts.simulate(Actions.SHUTTLE,state);
            Assertions.assertEquals(city,newState.getPlayerRepository().getCurrentPlayer().getCity());
        } catch (Exception e) {
            System.out.println("Shuttle");
            e.printStackTrace();
        }

        try{ //todo sometimes fails without reason
            City city = state.getCityRepository().getCities().get("Hong Kong");
            state.getPlayerRepository().getCurrentPlayer().addHand(new CityCard(city));
            state.getPlayerRepository().drive(state.getPlayerRepository().getCurrentPlayer(),city,state,false);
            IState newState = mcts.simulate(Actions.BUILD_RESEARCH_STATION,state);
            city = newState.getCityRepository().getCities().get("Hong Kong");
            assert(city.getResearchStation() != null);
        } catch (Exception e) {
            System.out.println("Build research station");
            e.printStackTrace();
        }

        try{
            City city = state.getPlayerRepository().getCurrentPlayer().getCity();

            int size = city.getCubes().size();
            if(size < 1){
                state.getDiseaseRepository().infect(city.getColor(),city);
                size = 1;
            }
            IState newState = mcts.simulate(Actions.TREAT_DISEASE,state);
            city = newState.getPlayerRepository().getCurrentPlayer().getCity();
            assert(size-1 == city.getCubes().size());
        } catch (Exception e) {
            System.out.println("Treat disease");
            e.printStackTrace();
        }

        try{
            City city = state.getCityRepository().getCities().get("Hong Kong");
            City finalCity = city;
            state.getPlayerRepository().getPlayers().values().forEach(p -> {
                try {
                    if(!p.equals(state.getPlayerRepository().getCurrentPlayer())){
                        state.getPlayerRepository().drive(p,finalCity,state,false);
                    }
                } catch (InvalidMoveException e) {
                    e.printStackTrace();
                }
            });
            IState newState = mcts.simulate(Actions.SHARE_KNOWLEDGE,state);

            city = newState.getCityRepository().getCities().get("Hong Kong");
            AtomicBoolean found = new AtomicBoolean();
            found.set(false);
            City finalCity1 = city;
            newState.getPlayerRepository().getPlayers().values().forEach(p -> {
                if(!p.equals(newState.getPlayerRepository().getCurrentPlayer())){
                    PlayerCard pc = p.getHand().stream().filter(c -> c instanceof CityCard && ((CityCard) c).getCity().equals(finalCity1)).findFirst().orElse(null);
                    if(pc != null) found.set(true);
                }
            });
            assert(found.get());
        } catch (Exception e) {
            System.out.println("Share");
            e.printStackTrace();
        }

        try{
            Player player = state.getPlayerRepository().getCurrentPlayer();
            City city = state.getCityRepository().getCities().get("Atlanta");
            player.addHand(new CityCard(city));
            player.addHand(new CityCard(city));
            player.addHand(new CityCard(city));
            player.addHand(new CityCard(city));
            player.addHand(new CityCard(city));

            state.getPlayerRepository().drive(state.getPlayerRepository().getCurrentPlayer(),city,state,false);

            IState newState = mcts.simulate(Actions.DISCOVER_CURE,state);
            AtomicBoolean cured = new AtomicBoolean(false);
            newState.getDiseaseRepository().getCures().values().forEach(c -> {
                if(c.isDiscovered()) cured.set(true);
            });
            assert(cured.get());
        } catch (Exception e) {
            System.out.println("Discover cure");
            e.printStackTrace();
        }

    }

    @Test
    public void functionalityTest(){
        ExperimentalGame game = new ExperimentalGame();
        IState state = game.getCurrentState();
        MCTS mcts = new MCTS(state,200);
        Actions a = mcts.run(state);
        System.out.println(a);
    }


}
