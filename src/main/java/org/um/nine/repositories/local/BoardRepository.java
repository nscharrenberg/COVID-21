package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.City;
import org.um.nine.domain.Cure;
import org.um.nine.exceptions.NoCubesLeftException;
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
    private IDiseaseRepository diseaseRepository;

    @Inject
    private RenderManager renderManager;

    @Override
    public void startGame() {
        renderBoard();
        cityRepository.reset();

        renderManager.renderCureMarker(new Cure(ColorRGBA.Red), new Vector3f(200, 0, 0), true);
        renderManager.renderCureMarker(new Cure(ColorRGBA.Yellow), new Vector3f(100, 0, 0));
        renderManager.renderCureMarker(new Cure(ColorRGBA.Cyan), new Vector3f(0, 0, 0));
        renderManager.renderCureMarker(new Cure(ColorRGBA.Magenta), new Vector3f(-100, 0, 0));
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
