package org.um.nine.domain;

public enum ActionType {
    DRIVE(0, "Move your pawn to any adjacent city."),
    DIRECT_FLIGHT(1, "Discard a city card and move your pawn to the discarded city card."),
    CHARTER_FLIGHT(2, "Discard the city card of your current city and move to any place on the board."),
    SHUTTLE(3, "Move from one research station to another."),
    BUILD_RESEARCH_STATION(4, "Discard the city card of your current city and build a research station."),
    TREAT_DISEASE(5, "Remove 1 disease cube from your current city."),
    SHARE_KNOWLEDGE(6,
            "Exchange the city card of your current city with another player that is in the same city as you. (or vise versa"),
    DISCOVER_CURE(7, "Use 5 city cards of the same color to discover a cure. (must be on a research station)"),
    NO_ACTION(8, "No action chosen!"), SKIP_ACTION(9, "Use this to skip an action");

    private final int id;
    private final String description;

    ActionType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
