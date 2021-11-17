package org.um.nine.agents.reinforcement.utils;

public class ActionPair {
    private String action;
    private String target;

    public ActionPair(String action, String target) {
        this.action = action;
        this.target = target;
    }

    public ActionPair(String action) {
        this.action = action;
        this.target = null;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
