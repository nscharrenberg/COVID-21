package org.um.nine.repositories.local;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import org.um.nine.Game;
import org.um.nine.Info;
import org.um.nine.Main;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.screens.MainMenuState;

import javax.inject.Inject;
import java.awt.*;

public class GameRepository implements IGameRepository {
    private Game app;
    private boolean isStarted = false;
    private Geometry backgroundGeom;
    private boolean isPaused = true;
    private Geometry map = null;
    private int speed = 200;

    @Inject
    private MainMenuState mainMenu;

    @Override
    public void init() {
        AppSettings settings = new AppSettings(true);
        settings.setTitle(Info.APP_TITLE);
        settings.setFrameRate(60);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        // Set the splash screen image
        settings.setSettingsDialogImage("images/image.jpg");
        settings.setResolution(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
        settings.setSamples(16);
        settings.setVSync(true);
        settings.setFullscreen(true);

        // Allow for touch screen devices
        settings.setEmulateMouse(true);

        app.setShowSettings(false);
        app.setSettings(settings);

        app.start();

        app.getStateManager().attach(mainMenu);
    }

    @Inject
    @Override
    public void setApp(Game app) {
        this.app = app;
    }

    @Override
    public void create() {
        app.getFlyByCamera().setEnabled(false);
        setBackgroundScreen();
        initLemur();
        addAmbientLight();
    }

    @Override
    public void update() {
    }

    private void initLemur() {
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
    }

    @Override
    public Game getApp() {
        return app;
    }

    @Override
    public AppSettings getSettings() {
        return app.getContext().getSettings();
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void setStarted(boolean started) {
        isStarted = started;
    }

    @Override
    public void start() {
        // First Clear the screen
        app.getRootNode().detachAllChildren();

        app.getFlyByCamera().setRotationSpeed(10);
        app.getFlyByCamera().setMoveSpeed(100);
        app.getFlyByCamera().setZoomSpeed(100);
        app.getCamera().setFrustumFar(3000);
        app.getCamera().setLocation(new Vector3f(0, 0,1500));

        inputHandling();

        // Initiate Game Graphics
        initiateGame();
    }

    private void addAmbientLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(3.5f));
        app.getRootNode().addLight(al);
    }

    /**
     * Loads in the main game objects such as the board, cards, pawns, etc...
     */
    private void initiateGame() {
        Box worldBox = new Box(1000, 500, 5);
        map = new Geometry("World", worldBox);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", app.getAssetManager().loadTexture("images/map.jpg"));
        mat.setTexture("NormalMap", app.getAssetManager().loadTexture("images/map_normal.png"));
        map.setMaterial(mat);
        app.getRootNode().attachChild(map);
    }

    private void setBackgroundScreen() {
        Material backgroundMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        backgroundMaterial.setTexture("DiffuseMap", app.getAssetManager().loadTexture("images/map.jpg"));
        backgroundMaterial.setTexture("NormalMap", app.getAssetManager().loadTexture("images/map_normal.png"));
        float w = app.getContext().getSettings().getWidth();
        float h = app.getContext().getSettings().getHeight();
        float ratio = w/h;

        app.getCamera().setLocation(Vector3f.ZERO.add(new Vector3f(0.0f, 0.0f, 100f)));
        float camZ = app.getCamera().getLocation().getZ() - 15;
        float width = camZ * ratio;
        float height = camZ;

        Quad fsq = new Quad(width, height);
        backgroundGeom = new Geometry("Background", fsq);
        backgroundGeom.setQueueBucket(RenderQueue.Bucket.Sky);
        backgroundGeom.setCullHint(Spatial.CullHint.Never);
        backgroundGeom.setMaterial(backgroundMaterial);
        backgroundGeom.setLocalTranslation(-(width / 2), -(height/ 2), 0);
        app.getRootNode().attachChild(backgroundGeom);
    }

    private void inputHandling() {
        app.getInputManager().addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        app.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping("ZoomIn", new KeyTrigger(KeyInput.KEY_MINUS));
        app.getInputManager().addMapping("ZoomOut", new KeyTrigger(KeyInput.KEY_EQUALS));

        app.getInputManager().addListener(actionListener, "Pause");
        app.getInputManager().addListener(analogListener, "Left", "Right", "Up", "Down", "ZoomIn", "ZoomOut");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                isPaused = !isPaused;
            }
        }
    };

    // TODO: Make a formula for smooth and easy navigation speed through the map
    // TODO: Make bounds so you don't go out of the map, or clip through the map or out of rendering distance.
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (map != null) {
                if (name.equals("Right")) {
                    Vector3f m = map.getLocalTranslation();
                    map.setLocalTranslation(m.x - value * speed, m.y, m.z);
                }
                if (name.equals("Left")) {
                    Vector3f m = map.getLocalTranslation();
                    map.setLocalTranslation(m.x + value * speed, m.y, m.z);
                }
                if (name.equals("Up")) {
                    Vector3f m = map.getLocalTranslation();
                    map.setLocalTranslation(m.x, m.y - value * speed, m.z);
                }

                if (name.equals("Down")) {
                    Vector3f m = map.getLocalTranslation();
                    map.setLocalTranslation(m.x, m.y + value * speed, m.z);
                }

                if (name.equals("ZoomIn")) {
                    Vector3f m = map.getLocalTranslation();
                    map.setLocalTranslation(m.x, m.y, m.z - value * speed);
                }

                if (name.equals("ZoomOut")) {
                    Vector3f m = map.getLocalTranslation();
                    map.setLocalTranslation(m.x, m.y, m.z + value * speed);
                }
            }
        }
    };
}
