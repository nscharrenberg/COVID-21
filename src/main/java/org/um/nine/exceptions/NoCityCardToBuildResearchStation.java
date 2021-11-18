package org.um.nine.exceptions;

import org.um.nine.domain.Player;

public class NoCityCardToBuildResearchStation extends Exception {
        private Player player;

    public NoCityCardToBuildResearchStation(Player player) {
            super(player.getName() + " does not have a city card to build the research station");
            this.player = player;
        }
    }
