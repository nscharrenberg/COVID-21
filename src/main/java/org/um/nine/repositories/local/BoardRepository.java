package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private City selectedCity;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Override
    public void startGame() {
        renderBoard();
        cityRepository.reset();
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

        BitmapText found = (BitmapText) gameRepository.getApp().getRootNode().getChild(textName);
        if (found != null) {
            found.setText(selectedCity != null ? selectedCity.getName() : "Nothing Selected");
            return;
        }

        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        BitmapText text = new BitmapText(myFont);
        text.setSize(myFont.getCharSet().getRenderedSize());
        text.setColor(ColorRGBA.Cyan);
        text.setText(selectedCity != null ? selectedCity.getName() : "Nothing Selected");
        text.setLocalTranslation(1, 1, 50);
        text.setName(textName);
        gameRepository.getApp().getRootNode().attachChild(text);
    }
}
