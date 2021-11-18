package org.um.nine.v1.utils.managers;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;

import java.util.Arrays;

public enum Input {
    UP("FLYCAM_Rise", new KeyTrigger(KeyInput.KEY_W)),
    DOWN("FLYCAM_Lower", new KeyTrigger(KeyInput.KEY_S)),
    RIGHT("FLYCAM_StrafeRight", new KeyTrigger(KeyInput.KEY_D)),
    LEFT("FLYCAM_StrafeLeft", new KeyTrigger(KeyInput.KEY_A)),
    ZOOM_IN("FLYCAM_ZoomIn", new KeyTrigger(KeyInput.KEY_MINUS), new MouseAxisTrigger(2, true)),
    ZOOM_OUT("FLYCAM_ZoomOut", new KeyTrigger(KeyInput.KEY_EQUALS), new MouseAxisTrigger(2, false)),
    PAUSE("SIMPLEAPP_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));

    Input(String name, Trigger... triggers) {
        this.name = name;
        this.triggers = triggers;
    }

    private final String name;
    private final Trigger[] triggers;

    public String getName() {
        return name;
    }

    public Trigger[] getTriggers() {
        return Arrays.copyOf(triggers, triggers.length);
    }
}
