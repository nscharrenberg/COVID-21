package org.um.nine.domain.roles;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Role;

import java.util.ArrayList;
import java.util.List;

public class OperationsExpertRole extends Role {
    static ColorRGBA lightGreen = new ColorRGBA(136f, 255f, 47f, 100f);

    public OperationsExpertRole() {
        super("Operations Expert", lightGreen);
    }

    @Override
    public RoleAction actions(int key) {
       if(key == 1) {
           return RoleAction.BUILD_RESEARCH_STATION;
       } else if (key == 2) {
           return RoleAction.MOVE_FROM_A_RESEARCH_STATION_TO_ANY_CITY;
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
