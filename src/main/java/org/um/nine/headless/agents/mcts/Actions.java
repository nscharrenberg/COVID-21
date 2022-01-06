package org.um.nine.headless.agents.mcts;

public enum Actions {
    DRIVE(0),
    DIRECT_FLIGHT(1),
    CHARTER_FLIGHT(2),
    SHUTTLE(3),
    BUILD_RESEARCH_STATION(4),
    TREAT_DISEASE(5),
    SHARE_KNOWLEDGE(6),
    DISCOVER_CURE(7),
    ROLE_ACTION(8);

    private final int ID;

    Actions(int id) {
        this.ID = id;
    }
}
