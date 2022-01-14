package org.um.nine.headless.game.domain.analytics;

import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.game.domain.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameAnalytics {
    private final int gameId;
    private int rounds = 0;
    private HashMap<String, PlayerAnalytics> playerAnalytics = new HashMap<>();
    private HashMap<Color, Integer> diseasesTreatedCount = new HashMap<>();
    private HashMap<Color, Integer> diseasesCuredCount = new HashMap<>();
    private HashMap<String, Integer> cityVisitedCount = new HashMap<>();
    private List<String> researchStationBuild = new ArrayList<>();
    private HashMap<String, Integer> researchStationUsed = new HashMap<>();
    private HashMap<String, Integer> roleActionUsed = new HashMap<>();
    private HashMap<String, Integer> roleEventUsed = new HashMap<>();
    private HashMap<String, Integer> actionsUsed = new HashMap<>();
    private HashMap<String, Integer> knowledgeShared = new HashMap<>();

    public GameAnalytics(int id) {
        this.gameId = id;

        GameStateFactory.getInitialState().getPlayerRepository().getPlayers().forEach((k, v) -> {
            this.playerAnalytics.put(v.getName(), new PlayerAnalytics(this.gameId, v));
        });
    }

    public void summarize() {
        playerAnalytics.forEach((k, v) -> {
//            summarizeDiseaseTreated(v);
//            summarizeDiseaseCured(v);
//            summarizeCityVisited(v);
//            summarizeResearchStationUsed(v);
//            summarizeRoleActionUsed(v);
            summarizeActionsUsed(v);
//            summarizeKnowledgeShared(v);
//            summarizeResearchStationsBuild(v);
//            summarizeRoleEventUsed(v);
        });
    }

    public PlayerAnalytics getPlayerAnalyticsByName(String name) {
        return this.playerAnalytics.get(name);
    }

    public PlayerAnalytics getCurrentPlayerAnalytics() {
        return getPlayerAnalyticsByName(GameStateFactory.getInitialState().getPlayerRepository().getCurrentPlayer().getName());
    }

    private void summarizeDiseaseTreated(PlayerAnalytics analytics) {
        analytics.getDiseasesTreatedCount().forEach((k, v) -> {
            int count = this.diseasesTreatedCount.get(k) + v;

            this.diseasesTreatedCount.put(k, count);
        });
    }

    private void summarizeDiseaseCured(PlayerAnalytics analytics) {
        analytics.getDiseasesCuredCount().forEach((k, v) -> {
            int count = this.diseasesCuredCount.get(k) + v;

            this.diseasesCuredCount.put(k, count);
        });
    }

    private void summarizeCityVisited(PlayerAnalytics analytics) {
        analytics.getCityVisitedCount().forEach((k, v) -> {
            int count = this.cityVisitedCount.get(k) + v;

            this.cityVisitedCount.put(k, count);
        });
    }

    private void summarizeResearchStationUsed(PlayerAnalytics analytics) {
        analytics.getResearchStationUsed().forEach((k, v) -> {
            int count = this.researchStationUsed.get(k) + v;

            this.researchStationUsed.put(k, count);
        });
    }

    private void summarizeRoleActionUsed(PlayerAnalytics analytics) {
        analytics.getRoleActionUsed().forEach((k, v) -> {
            int count = this.roleActionUsed.get(k) + v;

            this.roleActionUsed.put(k, count);
        });
    }

    private void summarizeRoleEventUsed(PlayerAnalytics analytics) {
        analytics.getRoleEventUsed().forEach((k, v) -> {
            int count = this.roleEventUsed.get(k) + v;

            this.roleEventUsed.put(k, count);
        });
    }

    private void summarizeActionsUsed(PlayerAnalytics analytics) {
        analytics.getActionsUsed().forEach((k, v) -> {
            int count = 0;

            if (this.actionsUsed.containsKey(k)) {
                count = this.actionsUsed.get(k);
            }

            this.actionsUsed.put(k, count + v);
        });
    }

    private void summarizeKnowledgeShared(PlayerAnalytics analytics) {
        analytics.getKnowledgeShared().forEach((k, v) -> {
            int count = this.knowledgeShared.get(k) + v;

            this.knowledgeShared.put(k, count);
        });
    }

    private void summarizeResearchStationsBuild(PlayerAnalytics analytics) {
        this.researchStationBuild.addAll(analytics.getResearchStationBuild());
    }

    public int getGame() {
        return gameId;
    }

    public int getRounds() {
        return rounds;
    }

    public HashMap<String, PlayerAnalytics> getPlayerAnalytics() {
        return playerAnalytics;
    }

    public void setPlayerAnalytics(HashMap<String, PlayerAnalytics> playerAnalytics) {
        this.playerAnalytics = playerAnalytics;
    }

    public HashMap<Color, Integer> getDiseasesTreatedCount() {
        return diseasesTreatedCount;
    }

    public void setDiseasesTreatedCount(HashMap<Color, Integer> diseasesTreatedCount) {
        this.diseasesTreatedCount = diseasesTreatedCount;
    }

    public HashMap<Color, Integer> getDiseasesCuredCount() {
        return diseasesCuredCount;
    }

    public void setDiseasesCuredCount(HashMap<Color, Integer> diseasesCuredCount) {
        this.diseasesCuredCount = diseasesCuredCount;
    }

    public HashMap<String, Integer> getCityVisitedCount() {
        return cityVisitedCount;
    }

    public void setCityVisitedCount(HashMap<String, Integer> cityVisitedCount) {
        this.cityVisitedCount = cityVisitedCount;
    }

    public List<String> getResearchStationBuild() {
        return researchStationBuild;
    }

    public void setResearchStationBuild(List<String> researchStationBuild) {
        this.researchStationBuild = researchStationBuild;
    }

    public HashMap<String, Integer> getResearchStationUsed() {
        return researchStationUsed;
    }

    public void setResearchStationUsed(HashMap<String, Integer> researchStationUsed) {
        this.researchStationUsed = researchStationUsed;
    }

    public HashMap<String, Integer> getRoleActionUsed() {
        return roleActionUsed;
    }

    public void setRoleActionUsed(HashMap<String, Integer> roleActionUsed) {
        this.roleActionUsed = roleActionUsed;
    }

    public HashMap<String, Integer> getRoleEventUsed() {
        return roleEventUsed;
    }

    public void setRoleEventUsed(HashMap<String, Integer> roleEventUsed) {
        this.roleEventUsed = roleEventUsed;
    }

    public HashMap<String, Integer> getActionsUsed() {
        return actionsUsed;
    }

    public void setActionsUsed(HashMap<String, Integer> actionsUsed) {
        this.actionsUsed = actionsUsed;
    }

    public HashMap<String, Integer> getKnowledgeShared() {
        return knowledgeShared;
    }

    public void setKnowledgeShared(HashMap<String, Integer> knowledgeShared) {
        this.knowledgeShared = knowledgeShared;
    }

    public void markNextRound() {
        this.rounds++;
    }
}
