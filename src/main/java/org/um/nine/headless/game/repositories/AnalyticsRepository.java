package org.um.nine.headless.game.repositories;

import org.um.nine.headless.game.contracts.repositories.IAnalyticsRepository;
import org.um.nine.headless.game.domain.analytics.GameAnalytics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

public class AnalyticsRepository implements IAnalyticsRepository {
   private int gameCount = 0;
   private int winCount = 0;

   private HashMap<Integer, GameAnalytics> gameAnalytics = new HashMap<>();

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
    public HashMap<Integer, GameAnalytics> getGameAnalytics() {
        return gameAnalytics;
    }

    @Override
    public void setGameAnalytics(HashMap<Integer, GameAnalytics> gameAnalytics) {
        this.gameAnalytics = gameAnalytics;
    }
}
