package org.um.nine.utils.managers;

import com.google.inject.Inject;
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
import org.um.nine.contracts.repositories.*;
import org.um.nine.exceptions.*;
import org.um.nine.screens.DialogBoxState;
import org.um.nine.screens.PauseMenu;

import java.util.concurrent.atomic.AtomicBoolean;


public class InputManager {
    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private CameraManager cameraManager;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private PauseMenu pauseMenu;

    public void clear() {
        gameRepository.getApp().getInputManager().clearMappings();
        gameRepository.getApp().getInputManager().clearRawInputListeners();
    }

    public void init() {
        gameRepository.getApp().getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
        gameRepository.getApp().getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

        //camera input mapping
        gameRepository.getApp().getFlyByCamera().unregisterInput();
        gameRepository.getApp().getInputManager().addMapping(Input.LEFT.getName(), Input.LEFT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.RIGHT.getName(), Input.RIGHT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.UP.getName(), Input.UP.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.DOWN.getName(), Input.DOWN.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.ZOOM_IN.getName(), Input.ZOOM_IN.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.ZOOM_OUT.getName(), Input.ZOOM_OUT.getTriggers());
        gameRepository.getApp().getInputManager().addMapping(Input.PAUSE.getName(), Input.PAUSE.getTriggers());

        //mouse input mapping
        gameRepository.getApp().getInputManager().addMapping("LClick",new MouseButtonTrigger(0));
        gameRepository.getApp().getInputManager().addMapping("RClick",new MouseButtonTrigger(1));
        gameRepository.getApp().getInputManager().addMapping("CClick",new MouseButtonTrigger(2));


        gameRepository.getApp().getInputManager().addListener(flyByCameraAnalogListener, Input.LEFT.getName(), Input.RIGHT.getName(), Input.UP.getName(), Input.DOWN.getName(), Input.ZOOM_IN.getName(), Input.ZOOM_OUT.getName());
        gameRepository.getApp().getInputManager().addListener(mouseButtonsListener,"LClick","RClick","CClick", Input.PAUSE.getName());

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
        if (name.equals("LClick")) {
            select();
        } else if (name.equals(Input.PAUSE.getName())) {
            System.out.println("Paused");
            gameRepository.getApp().getStateManager().attach(pauseMenu);
            pauseMenu.setEnabled(true);
        }
    };

    private void select() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = gameRepository.getApp().getInputManager().getCursorPosition();
        Vector3f click3d = gameRepository.getApp().getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = gameRepository.getApp().getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        gameRepository.getApp().getRootNode().collideWith(ray, results);
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

        this.cityRepository.getCities().forEach((key, city) -> {

            if (target.getName().equals(key)) {
                boardRepository.setSelectedCity(cityRepository.getCities().get(key));
                return;
            }

            if (city.getResearchStation() != null && target.getName().equals(city.getResearchStation().toString())) {
                boardRepository.setSelectedCity(cityRepository.getCities().get(key));
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
                boardRepository.setSelectedCity(cityRepository.getCities().get(key));
                return;
            }

            city.getPawns().forEach(p -> {
                if (target.getName().equals(p.toString())) {
                    found.set(true);
                    return;
                }
            });

            if (found.get()) {
                boardRepository.setSelectedCity(cityRepository.getCities().get(key));
                return;
            }
        });

        if(boardRepository.getSelectedCity() != null) {
            try {
                playerRepository.action(boardRepository.getSelectedPlayerAction());
            } catch (InvalidMoveException | NoActionSelectedException | ResearchStationLimitException | CityAlreadyHasResearchStationException e) {
                DialogBoxState dialog = new DialogBoxState(e.getMessage());
                gameRepository.getApp().getStateManager().attach(dialog);
                dialog.setEnabled(true);
            }
        }
    }





}
