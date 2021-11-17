package org.um.nine.utils;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;

public class Util {
    public static Vector3f calculateMenusize(Application application, Container container) {
        return calculateMenusize(application, container, 1.5f);
    }

    public static Vector3f calculateMenusize(Application application, Container container, float scale) {
        Vector3f standardScale = getStandardScale(container);
        standardScale = standardScale.mult(scale);

        return new Vector3f(standardScale.getX(), application.getContext().getSettings().getHeight(), 10);
    }

    public static Vector3f getStandardScale(Container container) {
        return container.getWorldScale();
    }
}
