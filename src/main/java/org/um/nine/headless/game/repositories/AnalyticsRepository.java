package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.domain.analytics.GameAnalytics;

import java.util.HashMap;

public class AnalyticsRepository {
   private int gameCount = 0;
   private int winCount = 0;

   private HashMap<Integer, GameAnalytics> gameAnalytics = new HashMap<>();

    /**
     * The game was won, add this to the analytics
     */
   public void won() {
       this.gameCount++;
       this.winCount++;
   }

    /**
     * The game was lost, add this to the analytics
     */
    public void lost() {
       this.gameCount++;
   }

    /**
     * Retrieving the total amount of games that were won
     * @return amount of games won
     */
    public int winCount() {
       return this.winCount;
    }

    /**
     * Retrieving the total amount of games that were lost
     * @return amount of games lost
     */
    public int lossCount() {
       return this.gameCount - this.winCount;
    }

    /**
     * Retrieving the total amount of games
     * @return total amount of games
     */
    public int getGameCount() {
       return this.gameCount;
    }

    /**
     * Retrieving the win rate compared to the total amount of played games
     * @return the win rate
     */
    public int winRate() {
       return this.winCount / this.getGameCount();
    }

    /**
     * Retrieving the loss rate compared to the total amount of played games
     * @return the loss rate
     */
    public int lossRate() {
       return this.lossCount() / this.getGameCount();
    }

    /**
     * Retrieving the ratio of the amount of games won compared to the amount of games lost
     * @return the win / loss ratio
     */
    public int winLossRatio() {
       return this.winCount() / this.lossCount();
    }

    public HashMap<Integer, GameAnalytics> getGameAnalytics() {
        return gameAnalytics;
    }

    public void setGameAnalytics(HashMap<Integer, GameAnalytics> gameAnalytics) {
        this.gameAnalytics = gameAnalytics;
    }
}
