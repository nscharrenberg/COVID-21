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
import org.um.nine.utils.managers.RenderManager;

import java.util.HashMap;

public class PlayerRepository implements IPlayerRepository {
    private HashMap<String, Player> players;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private RenderManager renderManager;

    public HashMap<String, Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player) throws PlayerLimitException {
        if (this.players.size() + 1 <= Info.PLAYER_THRESHOLD) {
            this.players.put(player.getName(), player);
            renderManager.renderPlayer(player);
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

}

