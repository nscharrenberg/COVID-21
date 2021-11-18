package org.um.nine.headless.game.domain.roles;

public enum RoleAction {
    MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN(1, "Move any pawn to other pawn", "As an action, move any pawn to a city with another pawn."),
    GIVE_PLAYER_CITY_CARD(2, "Give city card to player", "As an action, you may give (or a player can take) any City card from your hand. You must both be in the same city. The card does not have to match the city you are in"),
    TAKE_ANY_DISCARED_EVENT(3, "Take any discarded event", "As an action, take any discarded Event card and store it for future use."),
    BUILD_RESEARCH_STATION(4, "Build Research Station", "As an action, build a research station in the city you are in (no city card needed)"),
    MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY(5, "Move from Research station to any city", "Once per turn as an action, move from a research station to any city by discarding any city card"),
    NO_ACTION(0, "Nothing","No action chosen!");

    private final int id;
    private final String name;
    private final String description;

    RoleAction(int id, String name, String description) {
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
