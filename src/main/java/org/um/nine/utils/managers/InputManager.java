package org.um.nine.utils.managers;

import com.google.inject.Inject;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IGameRepository;


public class InputManager {
    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private CameraManager cameraManager;

    public void init() {
        gameRepository.getApp().getInputManager().addMapping(Input.PAUSE.getName(), Input.PAUSE.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.LEFT.getName(), Input.LEFT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.RIGHT.getName(), Input.RIGHT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.UP.getName(), Input.UP.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.DOWN.getName(), Input.DOWN.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.ZOOM_IN.getName(), Input.ZOOM_IN.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.ZOOM_OUT.getName(), Input.ZOOM_OUT.getTriggers());

        gameRepository.getApp().getInputManager().addListener(actionListener, Input.PAUSE.getName());
        gameRepository.getApp().getInputManager().addListener(analogListener, Input.UP.getName(), Input.LEFT.getName(), Input.RIGHT.getName(), Input.DOWN.getName(), Input.ZOOM_OUT.getName(), Input.ZOOM_IN.getName());
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                // TODO: Pause Logic
            }
        }
    };

    // TODO: Make a formula for smooth and easy navigation speed through the map
    // TODO: Make bounds so you don't go out of the map, or clip through the map or out of rendering distance.
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (boardRepository.getBoard() != null) {
                if (name.equals("Right")) {
                    cameraManager.localTranslateX(-value);
                }
                if (name.equals("Left")) {
                    cameraManager.localTranslateX(value);
                }
                if (name.equals("Up")) {
                    cameraManager.localTranslateY(-value);
                }

                if (name.equals("Down")) {
                    cameraManager.localTranslateY(value);
                }

                if (name.equals("ZoomIn")) {
                    cameraManager.localTranslateZ(value);
                }

                if (name.equals("ZoomOut")) {
                    cameraManager.localTranslateZ(-value);
                }
            }
        }
    };
}
