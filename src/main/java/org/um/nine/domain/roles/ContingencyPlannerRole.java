package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

public class ContingencyPlannerRole extends Role {
    public ContingencyPlannerRole() {
        super("Contingency Planner", ColorRGBA.Cyan);
    }

    @Override
    public void actions(String key) {
        // TODO For the price of one action you may take any card from the discard pile
        // (only 1 discarded card can be held in deck), the card doesn't count towards
        // the limit of 7 cards. after using the action card delete from game and then
        // you can pick another card from discard pile.
    }

    @Override
    public void events() {
        throw new UnsupportedOperationException("This Role does not have an event");
    }
}
