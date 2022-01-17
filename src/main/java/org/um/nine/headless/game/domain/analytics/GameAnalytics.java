package org.um.nine.headless.game.domain.analytics;

import org.um.nine.headless.agents.rhea.state.IState;
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
    private HashMap<String, Integer> macroActionsUsed = new HashMap<>();

    public GameAnalytics(int id, IState state) {
        this.gameId = id;

        state.getPlayerRepository().getPlayers().forEach((k, v) -> {
            this.playerAnalytics.put(v.getName(), new PlayerAnalytics(this.gameId, v));
        });
    }

    public void summarize() {
        playerAnalytics.forEach((k, v) -> {
            summarizeDiseaseTreated(v);
            summarizeDiseaseCured(v);
            summarizeCityVisited(v);
            summarizeResearchStationUsed(v);
            summarizeRoleActionUsed(v);
            summarizeActionsUsed(v);
            summarizeMacroActionsUsed(v);
            summarizeKnowledgeShared(v);
            summarizeResearchStationsBuild(v);
            summarizeRoleEventUsed(v);
        });
    }

    public PlayerAnalytics getPlayerAnalyticsByName(String name) {
        return this.playerAnalytics.get(name);
    }

    public PlayerAnalytics getCurrentPlayerAnalytics(IState state) {
        return getPlayerAnalyticsByName(state.getPlayerRepository().getCurrentPlayer().getName());
    }

    private void summarizeDiseaseTreated(PlayerAnalytics analytics) {
        analytics.getDiseasesTreatedCount().forEach((k, v) -> {
            int count = 0;

            if (this.diseasesTreatedCount.containsKey(k)) {
                count = this.diseasesTreatedCount.get(k);
            }

            this.diseasesTreatedCount.put(k, count + v);
        });
    }

    private void summarizeDiseaseCured(PlayerAnalytics analytics) {
        analytics.getDiseasesCuredCount().forEach((k, v) -> {
            int count = 0;

            if (this.diseasesCuredCount.containsKey(k)) {
                count = this.diseasesCuredCount.get(k);
            }

            this.diseasesCuredCount.put(k, count + v);
        });
    }

    private void summarizeCityVisited(PlayerAnalytics analytics) {
        analytics.getCityVisitedCount().forEach((k, v) -> {
            int count = 0;

            if (this.cityVisitedCount.containsKey(k)) {
                count = this.cityVisitedCount.get(k);
            }

            this.cityVisitedCount.put(k, count + v);
        });
    }

    private void summarizeResearchStationUsed(PlayerAnalytics analytics) {
        analytics.getResearchStationUsed().forEach((k, v) -> {
            int count = 0;

            if (this.researchStationUsed.containsKey(k)) {
                count = this.researchStationUsed.get(k);
            }

            this.researchStationUsed.put(k, count + v);
        });
    }

    private void summarizeRoleActionUsed(PlayerAnalytics analytics) {
        analytics.getRoleActionUsed().forEach((k, v) -> {
            int count = 0;

            if (this.roleActionUsed.containsKey(k)) {
                count = this.roleActionUsed.get(k);
            }

            this.roleActionUsed.put(k, count + v);
        });
    }

    private void summarizeRoleEventUsed(PlayerAnalytics analytics) {
        analytics.getRoleEventUsed().forEach((k, v) -> {
            int count = 0;

            if (this.roleEventUsed.containsKey(k)) {
                count = this.roleEventUsed.get(k);
            }

            this.roleEventUsed.put(k, count + v);
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

    private void summarizeMacroActionsUsed(PlayerAnalytics analytics) {
        analytics.getMacroActionsUsed().forEach((k, v) -> {
            int count = 0;

            if (this.macroActionsUsed.containsKey(k)) {
                count = this.macroActionsUsed.get(k);
            }

            this.macroActionsUsed.put(k, count + v);
        });
    }

    private void summarizeKnowledgeShared(PlayerAnalytics analytics) {
        analytics.getKnowledgeShared().forEach((k, v) -> {
            int count = 0;

            if (this.knowledgeShared.containsKey(k)) {
                count = this.knowledgeShared.get(k);
            }

            this.knowledgeShared.put(k, count + v);
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

    public HashMap<String, Integer> getMacroActionsUsed() {
        return macroActionsUsed;
    }

    public void setMacroActionsUsed(HashMap<String, Integer> macroActionsUsed) {
        this.macroActionsUsed = macroActionsUsed;
    }
}
