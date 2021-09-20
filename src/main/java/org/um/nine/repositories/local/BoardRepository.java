package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;

public class BoardRepository implements IBoardRepository {
    private Geometry board;

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
}
