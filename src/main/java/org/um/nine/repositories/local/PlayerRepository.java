package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Dome;
import com.jme3.util.BufferUtils;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.Player;
import org.um.nine.domain.roles.QuarantineSpecialistRole;
import org.um.nine.exceptions.PlayerLimitException;

import java.util.HashMap;

public class PlayerRepository implements IPlayerRepository {
    private HashMap<String, Player> players;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IGameRepository gameRepository;

    public HashMap<String, Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player) throws PlayerLimitException {
        if (this.players.size() + 1 <= Info.PLAYER_THRESHOLD) {
            this.players.put(player.getName(), player);
            renderPlayer(player);
        }
        throw new PlayerLimitException();
    }
    public void reset() {
        this.players = new HashMap<>();
        try {
            Player player = new Player("example", cityRepository.getCities().get("Atlanta"),false);
            player.setRole(new QuarantineSpecialistRole());
            addPlayer(player);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
    }
    private void renderPlayer(Player player){
        float offsetX = 20;
        float offsetY = 10;

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/pawn.j3o");
        model.rotate(45, 0, 0);
        model.setLocalTranslation(new Vector3f(player.getCity().getLocation().getX() + offsetX, player.getCity().getLocation().getY() + offsetY, player.getCity().getLocation().getZ() + 1));
        model.setLocalScale(.75f);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", player.getRole().getColor() ); // with Lighting.j3md
        mat.setColor("Ambient", player.getRole().getColor() ); // with Lighting.j3md
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }
}

