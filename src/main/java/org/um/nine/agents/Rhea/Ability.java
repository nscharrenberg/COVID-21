package org.um.nine.agents.Rhea;

import org.um.nine.domain.Cure;
import org.um.nine.utils.versioning.State;

public interface Ability {
    double abilityCure (State state, Cure cure);
}
