package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.agents.utils.IState;
import org.um.nine.headless.game.domain.Cure;

public interface Ability {
    double abilityCure (IState state, Cure cure);
}
