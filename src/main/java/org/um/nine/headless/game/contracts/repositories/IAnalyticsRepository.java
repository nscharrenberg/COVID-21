package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.analytics.GameAnalytics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

public interface IAnalyticsRepository {
    void won();

    void lost();

    int winCount();

    int lossCount();

    int getGameCount();

    int winRate();

    int lossRate();

    int winLossRatio();

    HashMap<Integer, GameAnalytics> getGameAnalytics();

    void setGameAnalytics(HashMap<Integer, GameAnalytics> gameAnalytics);
}
