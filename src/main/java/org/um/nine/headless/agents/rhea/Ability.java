package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.state.IState;

public interface Ability {
    double abilityCure (IState state, Cure cure);
}
