package org.um.nine.headless.agents.utils.actions;

import org.um.nine.headless.game.domain.ActionType;
import org.um.nine.headless.game.domain.City;

public record StandingAction(ActionType action, City applyTo) {
}
