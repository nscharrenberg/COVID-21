package org.um.nine.headless.game.repositories;

import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.game.contracts.repositories.IAnalyticsRepository;
import org.um.nine.headless.game.domain.analytics.GameAnalytics;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsRepository implements IAnalyticsRepository {
    public static boolean ENABLED = true;
   private int gameId = -1;
   private int gameCount = 0;
   private int winCount = 0;

   private List<GameAnalytics> gameAnalytics = new ArrayList<>();

   @Override
   public void start(IState state) {
       this.gameId = gameCount;
       gameAnalytics.add(new GameAnalytics(this.gameId, state));
   }

    /**
     * The game was won, add this to the analytics
     */
   @Override
   public void won() {
       this.gameCount++;
       this.winCount++;
   }

    /**
     * The game was lost, add this to the analytics
     */
    @Override
    public void lost() {
       this.gameCount++;
   }

    /**
     * Retrieving the total amount of games that were won
     * @return amount of games won
     */
    @Override
    public int winCount() {
       return this.winCount;
    }

    /**
     * Retrieving the total amount of games that were lost
     * @return amount of games lost
     */
    @Override
    public int lossCount() {
       return this.gameCount - this.winCount;
    }

    /**
     * Retrieving the total amount of games
     * @return total amount of games
     */
    @Override
    public int getGameCount() {
       return this.gameCount;
    }

    /**
     * Retrieving the win rate compared to the total amount of played games
     * @return the win rate
     */
    @Override
    public int winRate() {
        if (getGameCount() == 0) {
            return 0;
        }

       return this.winCount / this.getGameCount();
    }

    /**
     * Retrieving the loss rate compared to the total amount of played games
     * @return the loss rate
     */
    @Override
    public int lossRate() {
       if (getGameCount() == 0) {
           return 0;
       }

       return this.lossCount() / this.getGameCount();
    }

    /**
     * Retrieving the ratio of the amount of games won compared to the amount of games lost
     * @return the win / loss ratio
     */
    @Override
    public int winLossRatio() {
       if (this.lossCount() == 0 && this.winCount() == 0 || this.winCount() == 0) {
           return 0;
       }

       if (this.lossCount() == 0) {
           return 100;
       }

       return this.winCount() / this.lossCount();
    }

    @Override
    public List<GameAnalytics> getGameAnalytics() {
        return gameAnalytics;
    }

    @Override
    public GameAnalytics getCurrentGameAnalytics(IState state) {
        GameAnalytics found = getGameAnalyticById(this.gameId);

        if (found == null) {
            start(state);
        }

        return getGameAnalyticById(this.gameId);
    }

    @Override
    public GameAnalytics getGameAnalyticById(int id) {
        try {
            return this.gameAnalytics.get(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setGameAnalytics(List<GameAnalytics> gameAnalytics) {
        this.gameAnalytics = gameAnalytics;
    }

    @Override
    public int getGameId() {
        return gameId;
    }

    @Override
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
