package org.um.nine.agents.reinforcement;

import com.jme3.math.ColorRGBA;
import org.um.nine.agents.reinforcement.utils.ActionPair;
import org.um.nine.agents.reinforcement.utils.FeatureExtraction;
import org.um.nine.domain.ActionType;
import org.um.nine.domain.Disease;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.domain.roles.RoleEvent;
import org.um.nine.utils.versioning.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// TODO: We need to cover the whole action space, that is combination of each action with their corresponding city or disease.
//       We do not need to take account of a specific disease cube, only the color and can then just pick a random one of that color, as it'll not influence the outcome.
public class AllPossibleActions {
    private final List<String> actions = new ArrayList<>();
    private final List<ActionPair> actionPairs = new ArrayList<>();


    public AllPossibleActions() {
        retrieveActions();
    }

    public AllPossibleActions(FeatureExtraction features) {
        retrieveActions();

        ActionType.VALUES.forEach(a -> {
            if (a.equals(ActionType.TREAT_DISEASE) || a.equals(ActionType.DISCOVER_CURE)) {
                actionPairs.add(new ActionPair(a.toString(), ColorRGBA.Red.toString()));
                actionPairs.add(new ActionPair(a.toString(), ColorRGBA.Black.toString()));
                actionPairs.add(new ActionPair(a.toString(), ColorRGBA.Blue.toString()));
                actionPairs.add(new ActionPair(a.toString(), ColorRGBA.Yellow.toString()));
            } else if (a.equals(ActionType.SHARE_KNOWLEDGE)) {
                // TODO: share knowledge
            } else if (a.equals(ActionType.NO_ACTION) || a.equals(ActionType.SKIP_ACTION)) {
                actionPairs.add(new ActionPair(a.toString()));
            } else {
                features.getCities().forEach(c -> {
                    actionPairs.add(new ActionPair(a.toString(), c));
                });
            }
        });

        RoleAction.VALUES.forEach(a -> {
            if (a.equals(RoleAction.NO_ACTION)) {
                actionPairs.add(new ActionPair(a.toString()));
            }
        });

        RoleEvent.VALUES.forEach(a -> {

        });
    }

    private void retrieveActions() {
        actions.addAll(ActionType.VALUES.stream().map(ActionType::toString).collect(Collectors.toList()));
        actions.addAll(RoleAction.VALUES.stream().map(RoleAction::toString).collect(Collectors.toList()));
        actions.addAll(RoleEvent.VALUES.stream().map(RoleEvent::toString).collect(Collectors.toList()));
    }



    public List<ActionPair> getActions() {
        return Collections.unmodifiableList(actionPairs);
    }

    public int size() {
        return actionPairs.size();
    }
}
