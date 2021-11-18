package org.um.nine.headless.game.domain.cards;

import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.Player;

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