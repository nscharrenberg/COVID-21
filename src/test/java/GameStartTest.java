import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.Info;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;

public class GameStartTest {

    @Test
    public void start_success() throws Exception {
        String playerOne = "Player 1";
        String playerTwo = "Player 2";
        FactoryProvider.getPlayerRepository().createPlayer(playerOne, false);
        FactoryProvider.getPlayerRepository().createPlayer(playerTwo, false);
        FactoryProvider.getBoardRepository().start();

        Player playerOneClass = FactoryProvider.getPlayerRepository().getPlayers().get(playerOne);
        Player playerTwoClass = FactoryProvider.getPlayerRepository().getPlayers().get(playerTwo);
        Assertions.assertEquals(playerOneClass.getName(), playerOne);
        Assertions.assertEquals(playerTwoClass.getName(), playerTwo);
        Assertions.assertNotNull(playerOneClass.getRole());
        Assertions.assertNotNull(playerTwoClass.getRole());

        City atlanta = FactoryProvider.getCityRepository().getCities().get(Info.START_CITY);
        Assertions.assertEquals(FactoryProvider.getPlayerRepository().getPlayers().get(playerOne).getCity(), atlanta);
        Assertions.assertEquals(FactoryProvider.getPlayerRepository().getPlayers().get(playerTwo).getCity(), atlanta);

        FactoryProvider.getBoardRepository().setSelectedPlayerAction(ActionType.DRIVE);
        City newCity = FactoryProvider.getPlayerRepository().getCurrentPlayer().getCity().getNeighbors().get(0);
        FactoryProvider.getBoardRepository().setSelectedCity(newCity);
        FactoryProvider.getPlayerRepository().playerAction(FactoryProvider.getBoardRepository().getSelectedPlayerAction());

        Assertions.assertEquals(FactoryProvider.getPlayerRepository().getCurrentPlayer().getCity(), newCity);

        if (FactoryProvider.getPlayerRepository().getCurrentPlayer().equals(playerOneClass)) {
            Assertions.assertEquals(playerOneClass.getCity(), newCity);
        } else {
            Assertions.assertEquals(playerTwoClass.getCity(), newCity);
        }
    }
}
