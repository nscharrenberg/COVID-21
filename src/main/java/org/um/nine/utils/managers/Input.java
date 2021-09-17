package org.um.nine.utils.managers;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;

import java.util.Arrays;

public enum Input {
    UP("Up", new KeyTrigger(KeyInput.KEY_W)),
    DOWN("Down", new KeyTrigger(KeyInput.KEY_S)),
    RIGHT("Right", new KeyTrigger(KeyInput.KEY_D)),
    LEFT("Left", new KeyTrigger(KeyInput.KEY_A)),
    ZOOM_IN("ZoomIn", new KeyTrigger(KeyInput.KEY_EQUALS)),
    ZOOM_OUT("ZoomOut", new KeyTrigger(KeyInput.KEY_MINUS)),
    PAUSE("Pause", new KeyTrigger(KeyInput.KEY_P));

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
