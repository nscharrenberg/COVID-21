package org.um.nine.domain.roles;

import org.um.nine.domain.ActionType;

import java.util.List;

public enum RoleAction {
    MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN("As an action, move any pawn to a city with another pawn."),
    GIVE_PLAYER_CITY_CARD("As an action, you may give (or a player can take) any City card from your hand. You must both be in the same city. The card does not have to match the city you are in"),
    TAKE_ANY_DISCARED_EVENT("As an action, take any discarded Event card and store it for future use."),
    BUILD_RESEARCH_STATION("As an action, build a research station in the city you are in (no city card needed)"),
    MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY("Once per turn as an action, move from a research station to any city by discarding any city card"),
    NO_ACTION("No action chosen!");

    RoleAction(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }

    public static final List<RoleAction> VALUES = List.of(values());
    public static final int SIZE = VALUES.size();

    @Override
    public String toString() {
        return name;
    }
}
