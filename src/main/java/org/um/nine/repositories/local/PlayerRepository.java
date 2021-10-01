package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.renderer.RendererException;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.*;
import org.um.nine.domain.roles.*;
import org.um.nine.exceptions.ExternalMoveNotAcceptedException;
import org.um.nine.exceptions.InvalidMoveException;
import org.um.nine.exceptions.PlayerLimitException;
import org.um.nine.utils.managers.RenderManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class PlayerRepository implements IPlayerRepository {
    private HashMap<String, Player> players;
    private Stack<Role> availableRoles;

    private RoundState currentRoundState = null;

    private int actionsLeft = 4;
    private int drawLeft = 2;
    private int infectionLeft = 2;

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
        if (this.players.size() + 1 > Info.PLAYER_THRESHOLD) {
            throw new PlayerLimitException();
        }

        this.players.put(player.getName(), player);
    }

    @Override
    public void reset() {
        this.players = new HashMap<>();

        this.availableRoles = new Stack<>();
        availableRoles.add(new ContingencyPlannerRole());
        availableRoles.add(new DispatcherRole());
        availableRoles.add(new MedicRole());
        availableRoles.add(new OperationsExpertRole());
        availableRoles.add(new QuarantineSpecialistRole());
        availableRoles.add(new ResearcherRole());
        availableRoles.add(new ScientistRole());

        Collections.shuffle(availableRoles);
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

    /**
     * @return null if the turn has ended otherwise the RoundState of the turn
     */
    @Override
    public RoundState nextState(RoundState currentState){
        if (currentState == null) {
            this.currentRoundState = RoundState.ACTION;
            return RoundState.ACTION;
        } else if (currentState == RoundState.ACTION){
            actionsLeft--;
            if (actionsLeft == 0) {
                actionsLeft = 4;
                this.currentRoundState = RoundState.DRAW;
                return RoundState.DRAW;
            }

            this.currentRoundState = RoundState.ACTION;
            return RoundState.ACTION;
        } else if (currentState == RoundState.DRAW){
            drawLeft--;
            if (drawLeft == 0){
                drawLeft = 2;
                this.currentRoundState = RoundState.INFECT;
                return RoundState.INFECT;
            }

            this.currentRoundState = RoundState.DRAW;
            return RoundState.DRAW;
        } else if (currentState == RoundState.INFECT){
            infectionLeft--;
            if (infectionLeft == 0){
                infectionLeft = Objects.requireNonNull(diseaseRepository.getInfectionRate().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount();
                this.currentRoundState = null;
                return null;
            }

            this.currentRoundState = RoundState.INFECT;
            return RoundState.INFECT;
        }

        this.currentRoundState = null;
        throw new IllegalStateException();
    }

    @Override
    public RoundState getCurrentRoundState() {
        return currentRoundState;
    }

    @Override
    public void setCurrentRoundState(RoundState currentRoundState) {
        this.currentRoundState = currentRoundState;
    }

    @Override
    public void assignRoleToPlayer(Player player) {
        Role role = availableRoles.pop();
        player.setRole(role);
    }
}

