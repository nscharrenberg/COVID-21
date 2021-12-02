package org.um.nine.jme.utils.managers;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import org.um.nine.headless.game.GameStateFactory;
import org.um.nine.jme.JmeMain;
import org.um.nine.jme.screens.DialogBoxState;

import java.util.concurrent.atomic.AtomicBoolean;

public class InputManager {
    private CameraManager cameraManager = new CameraManager();

    public void clear() {
        JmeMain.getGameRepository().getApp().getInputManager().clearMappings();
    }

    public void init() {
        JmeMain.getGameRepository().getApp().getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
        JmeMain.getGameRepository().getApp().getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

        //camera input mapping
        JmeMain.getGameRepository().getApp().getFlyByCamera().unregisterInput();
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.LEFT.getName(), Input.LEFT.getTriggers());
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.RIGHT.getName(), Input.RIGHT.getTriggers());
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.UP.getName(), Input.UP.getTriggers());
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.DOWN.getName(), Input.DOWN.getTriggers());
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.ZOOM_IN.getName(), Input.ZOOM_IN.getTriggers());
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.ZOOM_OUT.getName(), Input.ZOOM_OUT.getTriggers());
        JmeMain.getGameRepository().getApp().getInputManager().addMapping(Input.PAUSE.getName(), Input.PAUSE.getTriggers());

        //mouse input mapping
        JmeMain.getGameRepository().getApp().getInputManager().addMapping("LClick",new MouseButtonTrigger(0));
        JmeMain.getGameRepository().getApp().getInputManager().addMapping("RClick",new MouseButtonTrigger(1));
        JmeMain.getGameRepository().getApp().getInputManager().addMapping("CClick",new MouseButtonTrigger(2));


        JmeMain.getGameRepository().getApp().getInputManager().addListener(flyByCameraAnalogListener, Input.LEFT.getName(), Input.RIGHT.getName(), Input.UP.getName(), Input.DOWN.getName(), Input.ZOOM_IN.getName(), Input.ZOOM_OUT.getName());
        JmeMain.getGameRepository().getApp().getInputManager().addListener(mouseButtonsListener,"LClick","RClick","CClick", Input.PAUSE.getName());

        JmeMain.getGameRepository().getApp().getFlyByCamera().setRotationSpeed(2);
        JmeMain.getGameRepository().getApp().getFlyByCamera().setMoveSpeed(200);
        JmeMain.getGameRepository().getApp().getFlyByCamera().setZoomSpeed(100);
        JmeMain.getGameRepository().getApp().getInputManager().setCursorVisible(true);
    }

    private final AnalogListener flyByCameraAnalogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (JmeMain.getGameRepository().getApp().getFlyByCamera().isEnabled()) {
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
        if (clicking) {
            if (name.equals("LClick")) {
                System.out.println("Clicked");
                select();
            } else if (name.equals(Input.PAUSE.getName())) {
//                JmeMain.getGameRepository().getApp().getStateManager().attach(pauseMenu);
//                pauseMenu.setEnabled(true);
            }
        }
    };

    private void select() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = JmeMain.getGameRepository().getApp().getInputManager().getCursorPosition();
        Vector3f click3d = JmeMain.getGameRepository().getApp().getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = JmeMain.getGameRepository().getApp().getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        JmeMain.getGameRepository().getApp().getRootNode().collideWith(ray, results);
        // (Print the results so we see what is going on:)
        for (int i = 0; i < results.size(); i++) {
            // (For each "hit", we know distance, impact point, geometry.)
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String target = results.getCollision(i).getGeometry().getName();
        }

        CollisionResult collisionResult = results.getClosestCollision();

        if (collisionResult == null) {
            return;
        }

        Geometry target = collisionResult.getGeometry();

        GameStateFactory.getInitialState().getCityRepository().getCities().forEach((key, city) -> {

            if (target.getName().equals(key)) {
                GameStateFactory.getInitialState().getBoardRepository().setSelectedCity(GameStateFactory.getInitialState().getCityRepository().getCities().get(key));
                return;
            }

            if (city.getResearchStation() != null && target.getName().equals(city.getResearchStation().toString())) {
                GameStateFactory.getInitialState().getBoardRepository().setSelectedCity(GameStateFactory.getInitialState().getCityRepository().getCities().get(key));
                return;
            }

            AtomicBoolean found = new AtomicBoolean(false);

            city.getCubes().forEach(c -> {
                if (target.getName().equals(c.toString())) {
                    found.set(true);
                    return;
                }
            });

            if (found.get()) {
                GameStateFactory.getInitialState().getBoardRepository().setSelectedCity(GameStateFactory.getInitialState().getCityRepository().getCities().get(key));
                return;
            }

            city.getPawns().forEach(p -> {
                if (target.getName().equals(p.toString())) {
                    found.set(true);
                    return;
                }
            });

            if (found.get()) {
                GameStateFactory.getInitialState().getBoardRepository().setSelectedCity(GameStateFactory.getInitialState().getCityRepository().getCities().get(key));
                return;
            }
        });

        if(GameStateFactory.getInitialState().getBoardRepository().getSelectedCity() != null) {
            try {
                GameStateFactory.getInitialState().getPlayerRepository().playerAction(GameStateFactory.getInitialState().getBoardRepository().getSelectedPlayerAction());
            } catch (Exception e) {
                DialogBoxState dialog = new DialogBoxState(e.getMessage());
                JmeMain.getGameRepository().getApp().getStateManager().attach(dialog);
                dialog.setEnabled(true);
            }
        }
    }
}
