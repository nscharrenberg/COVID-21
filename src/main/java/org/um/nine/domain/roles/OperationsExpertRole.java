package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class OperationsExpertRole extends Role {

    public OperationsExpertRole() { //light green
        super("Operations Expert", ColorRGBA.fromRGBA255(136, 255, 47, 1));
    }

    @Override
    public boolean actions(RoleAction key) {
        return key.equals(RoleAction.BUILD_RESEARCH_STATION) || key.equals(RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY);
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
