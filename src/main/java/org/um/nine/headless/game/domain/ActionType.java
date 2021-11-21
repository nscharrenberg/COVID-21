package org.um.nine.headless.game.domain;

public enum ActionType {
    DRIVE(0, "Drive", "Move your pawn to any adjacent city."),
    DIRECT_FLIGHT(1, "Direct Flight", "Discard a city card and move your pawn to the discarded city card."),
    CHARTER_FLIGHT(2, "Charter Flight", "Discard the city card of your current city and move to any place on the board."),
    SHUTTLE(3, "Shuttlf", "Move from one research station to another."),
    BUILD_RESEARCH_STATION(4, "Build Research Station", "Discard the city card of your current city and build a research station."),
    TREAT_DISEASE(5, "Treat Disease", "Remove 1 disease cube from your current city."),
    SHARE_KNOWLEDGE(6, "Share Knowledge",
                            "Exchange the city card of your current city with another player that is in the same city as you. (or vise versa"),
    DISCOVER_CURE(7, "Discover Cure", "Use 5 city cards of the same color to discover a cure. (must be on a research station)"),
    NO_ACTION(8, "No Action", "No action chosen!"),
    SKIP_ACTION(9, "Skip Turn", "Use this to skip an action");

    private final int id;
    private final String name;
    private final String description;

    ActionType(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}