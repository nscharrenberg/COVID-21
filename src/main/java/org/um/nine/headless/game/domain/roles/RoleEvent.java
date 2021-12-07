package org.um.nine.headless.game.domain.roles;

public enum RoleEvent {
    PREVENT_DISEASE_OR_OUTBREAK(0, "Prevent Outbreak","Prevent disease cube placements (and outbreaks) in the city you are in and all cities connected to it"),
    MOVE_OTHER_PLAYER(1, "Move other Player", "Move another player's pawn as if it were yours"),
    USE_STORED_EVENT_CARD(2, "Use Event Card", "When you play the stored event card, remove it from the game."),
    DISCOVER_CURE_FOUR_CARDS(3, "Discover CureMacro with 4 cards", "You need only 4 cards of the same color to do the Discover a CureMacro action"),
    REMOVE_ALL_CUBES_OF_A_COLOR(4, "Remove all cubes of a color", "Remove all cubes of one color when doing Treat Disease"),
    AUTO_REMOVE_CUBES_OF_CURED_DISEASE(5, "Auto remove cubes of cured disease", "Automatically remove cubes of cured diseases from the city you are in (and prevent them from being placed there)");

    private final int id;
    private final String name;
    private final String description;

    RoleEvent(int id, String name, String description) {
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

