package org.um.nine.utils.managers;

import com.google.inject.Inject;
import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
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
        gameRepository.getApp().getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);

        //camera input mapping
        gameRepository.getApp().getFlyByCamera().unregisterInput();
        gameRepository.getApp().getInputManager().addMapping(Input.LEFT.getName(), Input.LEFT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.RIGHT.getName(), Input.RIGHT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.UP.getName(), Input.UP.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.DOWN.getName(), Input.DOWN.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.ZOOM_IN.getName(), Input.ZOOM_IN.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.ZOOM_OUT.getName(), Input.ZOOM_OUT.getTriggers());

        //mouse input mapping
        gameRepository.getApp().getInputManager().addMapping("LClick",new MouseButtonTrigger(0));
        gameRepository.getApp().getInputManager().addMapping("RClick",new MouseButtonTrigger(1));
        gameRepository.getApp().getInputManager().addMapping("CClick",new MouseButtonTrigger(2));


        gameRepository.getApp().getInputManager().addListener(flyByCameraAnalogListener, Input.LEFT.getName(), Input.RIGHT.getName(), Input.UP.getName(), Input.DOWN.getName(), Input.ZOOM_IN.getName(), Input.ZOOM_OUT.getName());
        gameRepository.getApp().getInputManager().addListener(mouseButtonsListener,"LClick","RClick","CClick");

        gameRepository.getApp().getFlyByCamera().setRotationSpeed(2);
        gameRepository.getApp().getFlyByCamera().setMoveSpeed(200);
        gameRepository.getApp().getFlyByCamera().setZoomSpeed(100);
        gameRepository.getApp().getInputManager().setCursorVisible(true);
    }

    private final AnalogListener flyByCameraAnalogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (gameRepository.getApp().getFlyByCamera().isEnabled()) {
                if (name.equals(Input.UP.getName())) {
                    cameraManager.localTranslateY(value);
                } else if(name.equals(Input.DOWN.getName())) {
                    cameraManager.localTranslateY(-value);
                } else if (name.equals(Input.LEFT.getName())) {
                    cameraManager.localTranslateX(value);
                } else if (name.equals(Input.RIGHT.getName())) {
                    cameraManager.localTranslateX(-value);
                } else if (name.equals(Input.ZOOM_IN.getName())) {
                    cameraManager.localTranslateZ(value);
                } else if (name.equals(Input.ZOOM_OUT.getName())) {
                    cameraManager.localTranslateZ(-value);
                }
            }
        }
    };


    private final ActionListener mouseButtonsListener = (name, clicking, tpf) -> {
        switch (name) {
            case "LClick" -> System.out.println("Left Click");
            case "RClick" -> System.out.println("Right Click");
            case "CClick" -> System.out.println("Center Click");
        }
        Vector3f positionOfMouseOnClick = gameRepository.getApp().getCamera().getWorldCoordinates(
                gameRepository.getApp().getInputManager().getCursorPosition(),0);

        System.out.println("Mouse clicked on : "+ positionOfMouseOnClick);
    };





}
