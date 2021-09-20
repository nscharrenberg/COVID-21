package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Dome;
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
        Dome dome = new Dome(player.getCity().getLocation(),2,4,10);
        Geometry plate = new Geometry(player.getName(),dome);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color",player.getRole().getColor());
        mat.setColor("GlowColor", player.getRole().getColor());
        gameRepository.refreshFpp();
        plate.setMaterial(mat);
        plate.setLocalTranslation(player.getCity().getLocation());
        gameRepository.getApp().getRootNode().attachChild(plate);
    }
}

