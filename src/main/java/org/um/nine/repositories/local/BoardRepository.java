package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Cure;
import org.um.nine.domain.OutbreakMarker;
import org.um.nine.utils.managers.RenderManager;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private City selectedCity;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private RenderManager renderManager;

    @Override
    public void startGame() {
        renderBoard();
        cityRepository.reset();

        renderManager.renderCureMarker(new Cure(ColorRGBA.Red), new Vector3f(100, 0, 0), true);
        renderManager.renderCureMarker(new Cure(ColorRGBA.Yellow), new Vector3f(50, 0, 0));
        renderManager.renderCureMarker(new Cure(ColorRGBA.Cyan), new Vector3f(0, 0, 0));
        renderManager.renderCureMarker(new Cure(ColorRGBA.Magenta), new Vector3f(-50, 0, 0));
        renderOutbreakSection();
    }

    private void renderOutbreakSection() {
        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderManager.renderText("Outbreaks",new Vector3f(-975, -175, 2),ColorRGBA.White,"outbreaks-title-label",20,myFont);
        renderManager.renderOutbreakStar(new OutbreakMarker(0, ColorRGBA.White, true), new Vector3f(0, 0, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(1, ColorRGBA.fromRGBA255(255, 235, 238, 1)), new Vector3f(30, -30, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(2, ColorRGBA.fromRGBA255(255, 205, 210, 1)), new Vector3f(0, -60, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(3, ColorRGBA.fromRGBA255(239, 154, 154, 1)), new Vector3f(30, -90, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(4, ColorRGBA.fromRGBA255(229, 115, 115, 1)), new Vector3f(0, -120, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(5, ColorRGBA.fromRGBA255(229, 57, 53, 1)), new Vector3f(30, -150, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(6, ColorRGBA.fromRGBA255(211, 47, 47, 1)), new Vector3f(0, -180, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(7, ColorRGBA.fromRGBA255(198, 40, 40, 1)), new Vector3f(30, -210, 0));
        renderManager.renderOutbreakStar(new OutbreakMarker(8, ColorRGBA.fromRGBA255(183, 28, 28, 1)), new Vector3f(0, -240, 0));
    }

    @Override
    public Geometry getBoard() {
        if (board == null) {
            gameRepository.getApp().getRootNode().detachAllChildren();
            renderBoard();
        }
        return this.board;
    }

    private void renderBoard() {
        Box worldBox = new Box(1000, 500, 1);
        board = new Geometry("World", worldBox);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", gameRepository.getApp().getAssetManager().loadTexture("images/map.jpg"));
        mat.setTexture("NormalMap", gameRepository.getApp().getAssetManager().loadTexture("images/map_normal.png"));
        board.setMaterial(mat);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(3f));
        board.addLight(al);
        gameRepository.getApp().getRootNode().attachChild(board);
    }

    @Override
    public City getSelectedCity() {
        return selectedCity;
    }

    @Override
    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;

        String textName = "selected-city-text";

        renderManager.renderText(selectedCity != null ? selectedCity.getName() : "Nothing Selected", new Vector3f(0, 0, 5), ColorRGBA.White, textName);
    }
}
