package org.um.nine.domain.roles;

import org.um.nine.domain.Role;
import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.List;

public class ResearcherRole extends Role {

    public ResearcherRole() {
        super("Researcher", ColorRGBA.Brown);
    }

    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.GIVE_PLAYER_CITY_CARD);
    }

    @Override
    public boolean events(RoleEvent key) {
        return false;
    }

    @Override
    public List<RoleEvent> events() {
        return new ArrayList<>();
    }
}
