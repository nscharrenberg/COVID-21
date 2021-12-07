package test.agents;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.agents.Log;
import org.um.nine.headless.game.agents.baseline.BaselineAgent;
import org.um.nine.headless.game.exceptions.PlayerLimitException;
import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;

public class baselineAgentTests {

    @Test
    public void decisionsTest(){
        IPlayerRepository pr = FactoryProvider.getPlayerRepository();
        try{
            FactoryProvider.getPlayerRepository().createPlayer("P1", true);
            FactoryProvider.getPlayerRepository().createPlayer("P2", true);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        FactoryProvider.getBoardRepository().start();
        Log log = FactoryProvider.getPlayerRepository().getLog();
        BaselineAgent ba = new BaselineAgent();
        ba.randomAction(pr.getCurrentPlayer());
        int size = log.getLog().size();
        System.out.println("Log: " + size);
        System.out.println(log.toString());

        Assertions.assertEquals(pr.getCurrentPlayer().getName(),log.getLog().get(size-1).getCurrentPlayerName());
        Assertions.assertEquals(pr.getCurrentPlayer().getCity(),log.getLog().get(size-1).getTarget());

    }

    @Test
    public void repeatedDecisionsTest(){
        for(int i = 0; i < 1000; i++){
            decisionsTest();
        }
    }
}
