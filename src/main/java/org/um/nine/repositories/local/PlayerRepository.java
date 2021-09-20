package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
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
        Mesh mesh = new Mesh();
        float size = 10;
        float offsetX = -2.5f;
        float offsetY = -5;

        Vector3f[] vertices = new Vector3f[3];
        vertices[0] = new Vector3f(player.getCity().getLocation().getX() + offsetX, player.getCity().getLocation().getY() + offsetY, size);
        vertices[1] = new Vector3f(player.getCity().getLocation().getX() + size + offsetX,player.getCity().getLocation().getY() + offsetY,size);
        vertices[2] = new Vector3f(player.getCity().getLocation().getX() + offsetX + (size/2),player.getCity().getLocation().getY() + size + offsetY,size);

        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);

        int [] indexes = { 0,1,2};

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(VertexBuffer.Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();

        Geometry plate = new Geometry(player.getName(), mesh);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color",player.getRole().getColor());
        plate.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(plate);
    }
}

