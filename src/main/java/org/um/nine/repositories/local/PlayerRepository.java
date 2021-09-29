package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.math.ColorRGBA;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.*;
import org.um.nine.domain.roles.GenericRole;
import org.um.nine.domain.roles.RoleEvent;
import org.um.nine.exceptions.ExternalMoveNotAcceptedException;
import org.um.nine.exceptions.InvalidMoveException;
import org.um.nine.exceptions.PlayerLimitException;
import org.um.nine.utils.managers.RenderManager;

import java.util.HashMap;
import java.util.Objects;

import static org.um.nine.domain.RoundState.*;

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
        //Add players and give random roles
        City atlanta = cityRepository.getCities().get("Atlanta");
        int difficulty = 4;//Todo: get game info from setup menu
        int humans = 3;
        int bots = 1;
        int players = humans+bots;
        GenericRole[] roles = new GenericRole[players]; //keep track of roles? Not sure if needed
        String[] playerNames = {"Eric", "Noah", "Kai", "Drago"};
        String[] botNames = {"Cortana", "Jarvis", "Ultron", "Dave"};
        try {
            for(int i=0;i<humans;i++){
                Player player = new Player(playerNames[i], atlanta,false);
                player.setRole(new GenericRole("GenericBlue", ColorRGBA.Blue)); //Todo: add role assignment
                addPlayer(player);
            }
            for(int i=0;i<bots;i++){
                Player player = new Player(botNames[i], atlanta,true);
                player.setRole(new GenericRole("GenericRed", ColorRGBA.Red)); //Todo: add role assignment
                addPlayer(player);
            }
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


    int actionsLeft = 4;
    int drawLeft = 2;
    int infectionLeft = 2;

    /**
     * @return null if the turn has ended otherwise the RoundState of the turn
     */
    public RoundState nextState(RoundState currentState){
        if (currentState == null)
            return ACTION;
        else if (currentState == ACTION){
            actionsLeft--;
            if (actionsLeft == 0) {
                actionsLeft = 4;
                return DRAW;
            } else return ACTION;
        } else if (currentState == DRAW){
            drawLeft--;
            if (drawLeft == 0){
                drawLeft = 2;
                return INFECT;
            } else return DRAW;
        } else if (currentState == INFECT){
            infectionLeft--;
            if (infectionLeft == 0){
                infectionLeft = Objects.requireNonNull(diseaseRepository.getInfectionRate().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount();
                return null;
            } else return INFECT;
        }
        throw new IllegalStateException();
    }

}

