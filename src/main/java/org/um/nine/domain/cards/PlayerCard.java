package org.um.nine.domain.cards;

import org.um.nine.domain.Card;
import org.um.nine.domain.Player;

public class PlayerCard extends Card {
    private Player player;

    public PlayerCard(String name) {
        super(name);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
