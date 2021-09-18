package org.um.nine.domain.cards;

public class EpidemicCard extends PlayerCard {
    public EpidemicCard(String name) {
        super(name);
    }

    public void increase() {
        // TODO: Move the infection rate indicator up by 1
    }

    public void infect() {
        // TODO: Draw a card off  the bottom of the infection draw pile and infect the indicated city with 3 cubes. Discard the card.
    }

    public void intensify() {
        // TODO: Shuffle the infection discard pile and place it on top of the infection draw pile.
    }
}
