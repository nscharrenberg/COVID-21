package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Cure;
import org.um.nine.domain.Player;
import org.um.nine.domain.roles.GenericRole;
import org.um.nine.domain.roles.QuarantineSpecialistRole;
import org.um.nine.domain.roles.RoleEvent;
import org.um.nine.exceptions.ExternalMoveNotAcceptedException;
import org.um.nine.exceptions.InvalidMoveException;
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
    private IDiseaseRepository diseaseRepository;

    @Inject
    private RenderManager renderManager;

    public HashMap<String, Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player) throws PlayerLimitException {
        if(this.players==null) this.players=new HashMap<>();
        if (this.players.size() + 1 > Info.PLAYER_THRESHOLD) {
            throw new PlayerLimitException();
        }

        this.players.put(player.getName(), player);
        renderManager.renderPlayer(player, player.getCity().getPawnPosition(player));

    }

    public void reset() {
        this.players = new HashMap<>();
        try {
            City city = cityRepository.getCities().get("Atlanta");
            Player player = new Player("example", city,false);
            player.setRole(new QuarantineSpecialistRole());
            addPlayer(player);

            Player playerTwo = new Player("example2", city, false);
            playerTwo.setRole(new GenericRole("GenericOne", ColorRGBA.Red));
            addPlayer(playerTwo);

            Player playerThree = new Player("example3", city, false);
            playerThree.setRole(new GenericRole("GenericTwo", ColorRGBA.Blue));
            addPlayer(playerThree);

            Player playerFour = new Player("example4", city, false);
            playerFour.setRole(new GenericRole("GenericThree", ColorRGBA.Yellow));
            addPlayer(playerFour);
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void move(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        if(player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            // TODO: Allow Move normally
        } else if (!player.getCity().getNeighbors().contains(city)) {
            // TODO: allow player to discard a card or not
        }

        city.addPawn(player);

        if (player.getRole().events(RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE)) {
            city.getCubes().forEach(c -> {
                Cure found = diseaseRepository.getCures().get(c.getColor());

                if (found != null) {
                    if (found.isDiscovered()) {
                        city.getCubes().removeIf(cb -> cb.getColor().equals(found.getColor()));
                    }
                }
            });
        }

        renderManager.renderPlayer(player, city.getPawnPosition(player));
    }

    @Override
    public void verifyExternalMove(Player instigator, Player target, City city, boolean accept) throws InvalidMoveException, ExternalMoveNotAcceptedException {
        if (instigator.equals(target)) {
            move(instigator, city);
            return;
        }

        if (accept) {
            throw new ExternalMoveNotAcceptedException(instigator, target, city);
        }

        move(target, city);
    }
}

