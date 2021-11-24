package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.utils.State;
import org.um.nine.headless.game.domain.Cure;

public interface Ability {
    double abilityCure (State state, Cure cure);
}
