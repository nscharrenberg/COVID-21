package org.um.nine.agents.reinforcement;

import org.um.nine.domain.ActionType;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.domain.roles.RoleEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AllPossibleActions {
    private final List<String> actions = new ArrayList<>();

    public AllPossibleActions() {
        actions.addAll(ActionType.VALUES.stream().map(ActionType::toString).collect(Collectors.toList()));
        actions.addAll(RoleAction.VALUES.stream().map(RoleAction::toString).collect(Collectors.toList()));
        actions.addAll(RoleEvent.VALUES.stream().map(RoleEvent::toString).collect(Collectors.toList()));
    }

    public List<String> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public int size() {
        return actions.size();
    }
}
