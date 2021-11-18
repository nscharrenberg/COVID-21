package org.um.nine.v1.domain.roles;

public enum RoleEvent {
    PREVENT_DISEASE_OR_OUTBREAK("Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it"),
    MOVE_OTHER_PLAYER("Move another player's pawn as if it were yours"),
    USE_STORED_EVENT_CARD("When you play the stored event card, remove it from the game."),
    DISCOVER_CURE_FOUR_CARDS("You need only 4 cards of the same color to do the Discover a Cure action"),
    REMOVE_ALL_CUBES_OF_A_COLOR("Remove all cubes of one color when doing Treat Disease"),
    AUTO_REMOVE_CUBES_OF_CURED_DISEASE("Automatically remove cubes of cured diseases from the city you are in (and prevent them from being placed there)");

    RoleEvent(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
