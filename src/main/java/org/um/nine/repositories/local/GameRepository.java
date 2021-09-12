package org.um.nine.repositories.local;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import org.um.nine.Game;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.Player;
import org.um.nine.domain.RESOLUTION;
import org.um.nine.screens.MainMenuState;

import java.util.HashMap;

public class GameRepository implements IGameRepository {
    private final Game app;
    private boolean isStarted = false;
    private Geometry backgroundGeom;
    private HashMap<String, Player> players = new HashMap<>();

    public GameRepository() {
        this.app = new Game();
        app.getStateManager().attach(new MainMenuState());

        players.put("PlayerOne", new Player("Player One"));
        players.put("PlayerTwo", new Player("Player Two"));
    }

    @Override
    public void init() {
        AppSettings settings = new AppSettings(true);
        settings.setTitle(Info.APP_TITLE);
        settings.setFrameRate(60);

        // Set the splash screen image
        settings.setSettingsDialogImage("images/image.jpg");
        settings.setResolution(RESOLUTION.RES_1080.getWidth(), RESOLUTION.RES_1080.getHeight());
        settings.setSamples(16);
        settings.setVSync(false);
        settings.setFullscreen(true);

        // Allow for touch screen devices
        settings.setEmulateMouse(true);

//        app.setShowSettings(false);
        app.setSettings(settings);

        app.start();
    }

    @Override
    public void create() {
        app.getFlyByCamera().setEnabled(false);
        setBackgroundScreen();
        initLemur();
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

    private void setBackgroundScreen() {
        Material backgroundMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        backgroundMaterial.setTexture("ColorMap", app.getAssetManager().loadTexture("images/map.jpg"));
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

    @Override
    public HashMap<String, Player> getPlayers() {
        return players;
    }
}
