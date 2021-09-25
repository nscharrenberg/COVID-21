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
    public RoleAction actions(int key) {
       if (key == 1) {
           return RoleAction.GIVE_PLAYER_CITY_CARD;
       }

       return null;
    }

    @Override
    public RoleEvent events(int key) {
        return null;
    }

    @Override
    public List<RoleEvent> events() {
        return new ArrayList<>();
    }
}
