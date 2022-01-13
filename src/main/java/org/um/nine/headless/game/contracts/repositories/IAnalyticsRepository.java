package org.um.nine.headless.game.contracts.repositories;

import org.um.nine.headless.game.domain.analytics.GameAnalytics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;

public interface IAnalyticsRepository {
    void won();

    void lost();

    int winCount();

    int lossCount();

    int getGameCount();

    int winRate();

    int lossRate();

    int winLossRatio();

    List<GameAnalytics> getGameAnalytics();

    void setGameAnalytics(List<GameAnalytics> gameAnalytics);
}
