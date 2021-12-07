package org.um.nine.jme.repositories;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.headless.game.Settings;
import org.um.nine.jme.JmeGame;
import org.um.nine.jme.screens.MainMenuState;

import java.awt.*;

public class GameRepository {
    private JmeGame app;
    private Geometry backgroundGeom;
    private int speed = 200;

    public void init() {
        AppSettings settings = new AppSettings(true);
        settings.setTitle(Settings.APP_TITLE);
        settings.setFrameRate(60);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        // Set the splash screen image
        settings.setSettingsDialogImage("images/image.jpg");
        settings.setResolution(	gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight() - 150);
        settings.setSamples(16);
        settings.setVSync(true);
        settings.setGraphicsDebug(false);
        settings.setFullscreen(false);

        // Allow for touch screen devices
        settings.setEmulateMouse(true);

        app.setShowSettings(false);
        app.setSettings(settings);
        app.setDisplayStatView(false);
        app.start();

        app.getStateManager().attach(new MainMenuState());
    }

    public void create() {
        app.getFlyByCamera().setEnabled(false);
        setBackgroundScreen();
        initLemur();
        addAmbientLight();
    }

    public void update() {

    }

    public void start() {
        app.getFlyByCamera().setEnabled(true);
        // First Clear the screen
        app.getRootNode().detachAllChildren();

        app.getCamera().setFrustumFar(3000);
        app.getCamera().setLocation(new Vector3f(0, 0,1500));

//        inputManager.init();

        // Initiate Game Graphics
        GameStateFactory.getInitialState().getBoardRepository().reset();
        GameStateFactory.getInitialState().getBoardRepository().start();
    }

    private void addAmbientLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(.25f));
        app.getRootNode().addLight(al);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-0.5f, -0.5f, 0).normalizeLocal());
        app.getRootNode().addLight(sun);
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

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(3f));
        backgroundGeom.addLight(al);
        app.getRootNode().attachChild(backgroundGeom);
    }

    private void initLemur() {
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
    }



    public AppSettings getSettings() {
        return app.getContext().getSettings();
    }

    public JmeGame getApp() {
        return app;
    }

    public void setApp(JmeGame app) {
        this.app = app;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void cleanup() {
        speed = 200;
        //TODO add cleanup to everything
        //GameStateFactory.getInitialState().getBoardRepository().cleanup();
        backgroundGeom = null;
        app.getRootNode().detachAllChildren();
        app.restart();
        create();
    }
}
