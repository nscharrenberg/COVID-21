package org.um.nine.headless.game.domain.cards;

import org.um.nine.headless.game.domain.Card;
import org.um.nine.headless.game.domain.Player;

import java.util.Objects;

public class PlayerCard extends Card {
    private Player player;

    @Override
    public PlayerCard clone() {
        PlayerCard clone = (PlayerCard) super.clone();
        clone.setPlayer(this.getPlayer());
        return clone;
    }

    public PlayerCard(String name) {
        super(name);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerCard that = (PlayerCard) o;

        return Objects.equals(player, that.player);
    }


}
