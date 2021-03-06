package org.um.nine.headless.game.domain.analytics;

import org.um.nine.headless.agents.rhea.macro.MacroAction;
import org.um.nine.headless.game.domain.*;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.headless.game.domain.roles.RoleEvent;
import org.um.nine.headless.game.repositories.AnalyticsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerAnalytics {
    private int gameId;
    private String player;
    private String role;
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

    public PlayerAnalytics(int id, Player player) {
        this.gameId = id;
        this.player = player.getName();
        this.role = player.getRole().getName();
    }

    public void markDiseaseTreat(Color color) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.diseasesTreatedCount.containsKey(color)) {
            count = this.diseasesTreatedCount.get(color);
        }

        this.diseasesTreatedCount.put(color, count + 1);
    }

    public void markDiseaseCure(Color color) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.diseasesCuredCount.containsKey(color)) {
            count = this.diseasesCuredCount.get(color);
        }

        this.diseasesCuredCount.put(color, count + 1);
    }

    public void markCityVisited(City city) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.cityVisitedCount.containsKey(city.getName())) {
            count = this.cityVisitedCount.get(city.getName());
        }

        this.cityVisitedCount.put(city.getName(), count + 1);
    }

    public void markResearchStationBuild(City city) {
        if (!AnalyticsRepository.ENABLED) return;

        this.researchStationBuild.add(city.getName());
    }

    public void markUseResearchStation(City city) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.researchStationUsed.containsKey(city.getName())) {
            count = this.researchStationUsed.get(city.getName());
        }

        this.researchStationUsed.put(city.getName(), count + 1);
    }

    public void markRoleActionUsed(RoleAction action) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.roleActionUsed.containsKey(action.getName())) {
            count = this.roleActionUsed.get(action.getName());
        }

       this.roleActionUsed.put(action.getName(), count + 1);
    }

    public void markRoleEventUsed(RoleEvent event) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.getRoleEventUsed().containsKey(event.getName())) {
            count = this.actionsUsed.get(event.getName());
        }

        this.roleEventUsed.put(event.getName(), count + 1);
    }

    public void markActionTypeUsed(ActionType actionType) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.getActionsUsed().containsKey(actionType.getName())) {
            count = this.actionsUsed.get(actionType.getName());
        }

        this.actionsUsed.put(actionType.getName(), count + 1);
    }

    public void markMacroActionUsed(MacroAction macroAction) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.getMacroActionsUsed().containsKey(macroAction.toString())) {
            count = this.macroActionsUsed.get(macroAction.toString());
        }

        this.macroActionsUsed.put(macroAction.toString(), count + 1);
    }

    public void shareKnowledge(PlayerCard card) {
        if (!AnalyticsRepository.ENABLED) return;

        int count = 0;

        if (this.knowledgeShared.containsKey(card.getName())) {
            count = this.knowledgeShared.get(card.getName());
        }

        this.knowledgeShared.put(card.getName(), count + 1);
    }

    public int getGame() {
        return gameId;
    }

    public String getPlayer() {
        return player;
    }

    public String getRole() {
        return role;
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

    public HashMap<String, Integer> getMacroActionsUsed() {
        return macroActionsUsed;
    }

    public void setMacroActionsUsed(HashMap<String, Integer> macroActionsUsed) {
        this.macroActionsUsed = macroActionsUsed;
    }
}
