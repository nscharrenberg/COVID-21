import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.ExperimentalGame;
import org.um.nine.headless.game.Settings;
import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.domain.Player;

public class GameStartTest {

    @Test
    public void startSuccess() {
        Assumptions.assumingThat(Settings.DEFAULT_INITIAL_STATE, this::startDefault);
        Assumptions.assumingThat(!Settings.DEFAULT_INITIAL_STATE, this::startConfigured);

    }

    public void startDefault() throws Exception {
        Assumptions.assumeTrue(Settings.DEFAULT_INITIAL_STATE);

        String playerOne = "Bot 1";
        String playerTwo = "Bot 2";
        String playerThree = "Bot 3";
        String playerFour = "Bot 4";

        new ExperimentalGame().start();
        IState initialState = GameStateFactory.getInitialState();

        Player playerOneClass = initialState.getPlayerRepository().getPlayers().get(playerOne);
        Player playerTwoClass = initialState.getPlayerRepository().getPlayers().get(playerTwo);
        Player playerThreeClass = initialState.getPlayerRepository().getPlayers().get(playerThree);
        Player playerFourClass = initialState.getPlayerRepository().getPlayers().get(playerFour);
        Assertions.assertEquals(playerOneClass.getName(), playerOne);
        Assertions.assertEquals(playerTwoClass.getName(), playerTwo);
        Assertions.assertEquals(playerThreeClass.getName(), playerThree);
        Assertions.assertEquals(playerFourClass.getName(), playerFour);
        Assertions.assertNotNull(playerOneClass.getRole());
        Assertions.assertNotNull(playerTwoClass.getRole());
        Assertions.assertNotNull(playerThreeClass.getRole());
        Assertions.assertNotNull(playerFourClass.getRole());

        City atlanta = initialState.getCityRepository().getCities().get(Settings.START_CITY);
        Assertions.assertEquals(initialState.getPlayerRepository().getPlayers().get(playerOne).getCity(), atlanta);
        Assertions.assertEquals(initialState.getPlayerRepository().getPlayers().get(playerTwo).getCity(), atlanta);
        Assertions.assertEquals(initialState.getPlayerRepository().getPlayers().get(playerThree).getCity(), atlanta);
        Assertions.assertEquals(initialState.getPlayerRepository().getPlayers().get(playerFour).getCity(), atlanta);

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


    public void startConfigured() throws Exception {
        Assumptions.assumeFalse(Settings.DEFAULT_INITIAL_STATE);

        String playerOne = "Player 1";
        String playerTwo = "Player 2";
        IState initialState = GameStateFactory.createInitialState();

        initialState.getBoardRepository().reset();
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
