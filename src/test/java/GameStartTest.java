import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.Player;

public class GameStartTest {

    @Test
    public void start_success() throws Exception {
        String playerOne = "Player 1";
        String playerTwo = "Player 2";
        IState initialState = GameStateFactory.createInitialState();
        initialState.getPlayerRepository().createPlayer(playerOne, false);
        initialState.getPlayerRepository().createPlayer(playerTwo, false);
        initialState.getBoardRepository().setDifficulty(Difficulty.EASY);
        initialState.getBoardRepository().start();

        Player playerOneClass = initialState.getPlayerRepository().getPlayers().get(playerOne);
        Player playerTwoClass = initialState.getPlayerRepository().getPlayers().get(playerTwo);
        Assertions.assertEquals(playerOneClass.getName(), playerOne);
        Assertions.assertEquals(playerTwoClass.getName(), playerTwo);
        Assertions.assertNotNull(playerOneClass.getRole());
        Assertions.assertNotNull(playerTwoClass.getRole());

        City atlanta = initialState.getCityRepository().getCities().get(Settings.START_CITY);
        Assertions.assertEquals(initialState.getPlayerRepository().getPlayers().get(playerOne).getCity(), atlanta);
        Assertions.assertEquals(initialState.getPlayerRepository().getPlayers().get(playerTwo).getCity(), atlanta);

        initialState.getBoardRepository().setSelectedPlayerAction(ActionType.DRIVE);
        City newCity = initialState.getPlayerRepository().getCurrentPlayer().getCity().getNeighbors().get(0);
        initialState.getBoardRepository().setSelectedCity(newCity);
        initialState.getPlayerRepository().playerAction(initialState.getBoardRepository().getSelectedPlayerAction());

        Assertions.assertEquals(initialState.getPlayerRepository().getCurrentPlayer().getCity(), newCity);

        if (initialState.getPlayerRepository().getCurrentPlayer().equals(playerOneClass)) {
            Assertions.assertEquals(playerOneClass.getCity(), newCity);
        } else {
            Assertions.assertEquals(playerTwoClass.getCity(), newCity);
        }
    }
}
