package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Scientist;

import java.util.Comparator;
import java.util.List;

import static com.google.common.primitives.Doubles.min;

public class StateEvaluation {


    /**
     * A(c(t))
     * Ability to discover a cure based on the number of cards of that color in hand
     * Will evaluate over 5 cards, 4 if the role of the current player is Scientist role
     */
    public static double abilityCure(Color color, Player p) {
        double hpt = sameColorCards(p.getHand(), color);
        double cd = Cd(p);
        if (hpt >= cd) return 1;
        return hpt / cd;
    }

    public static CityCard findMostValuableCityCardForPlayer(Player givingPlayer, Player takingPlayer) {
        final CityCard c = (CityCard) givingPlayer.getHand().get(0);
        return givingPlayer.getHand().
                stream().
                map(pc -> (CityCard) pc).
                max(Comparator.comparingDouble(cc -> StateEvaluation.abilityCure(cc.getCity().getColor(), takingPlayer))).
                orElse(c);
    }


    public static double abilityCure2(IState state, Color color) {
        if (state.getDiseaseRepository().getCures().get(color).isDiscovered()) return 1;
        return state.getPlayerRepository().getPlayers().values().stream().map(p -> abilityCure(color, p)).max(Double::compareTo).get();
    }

    public static double sameColorCards(List<PlayerCard> cards, Color color) {
        return cards.stream().
                filter(c -> c instanceof CityCard cc &&
                        cc.getCity().getColor().equals(color)).
                count();
    }

    /**
     * A(t)
     * Ability to find a cure based on it being already discovered or not
     */
    public static double abilityCure(IState state, Color color) {
        if (state.getDiseaseRepository().getCures().get(color).isDiscovered()) return 1;

            //TODO : this is other players Ac ability to cure the disease.. without share knowledge
            // this value is completely irrelevant
        else return state.getPlayerRepository().
                getPlayers().
                values().
                stream().
                map(p -> abilityCure(color, p)).
                max(Double::compareTo).
                orElse(0d);
    }

    /**
     * Number of cards needed to discover a cure
     * @param player the player with the role
     * @return 4 if the role is Scientist, else 5
     */
    public static int Cd (Player player){
        if (player.getRole() instanceof Scientist) return 4;
        else return 5;
    }


    /**
     * Evaluate the state based on the number of cures discovered
     */
    public static StateHeuristic Fod = state -> (double) state.getDiseaseRepository()
            .getCures().values()
            .stream().filter(Cure::isDiscovered)
            .count() /4;


    /**
     * Evaluate the state based on the number of cures discovered and the ability to cure
     * a disease based on the number of cards in hand
     */
    public static StateHeuristic FoA = state -> {

        final double [] sA = new double[]{0};
        double Nd = (double) state.getDiseaseRepository()
                .getCures().values()
                .stream().filter(Cure::isDiscovered)
                .count();

        state.getDiseaseRepository().
                getCures().values().forEach(cure -> sA[0] += abilityCure(state, cure.getColor()) + (0.3 * Nd));
        return sA[0] / 4 * 1.3d;
    };


    /**
     * Evaluate the state based on the average number of diseases of each color
     */
    public StateHeuristic Fca = state -> {
        final double[] s = new double[]{0};
        state.getDiseaseRepository()
                .getCubes().values()
                .forEach((diseases) -> s[0] += diseases.size()/24.);
        return s[0] / 4d;
    };


    /**
     * Evaluate the state based on the minimum number of diseases of each color
     */
    public static StateHeuristic Fcm = state -> {
        double red, blue, black, yellow;
        red = state.getDiseaseRepository().getCubes().get(Color.RED).size() / 24d;
        blue = state.getDiseaseRepository().getCubes().get(Color.BLUE).size() / 24d;
        black = state.getDiseaseRepository().getCubes().get(Color.BLACK).size() / 24d;
        yellow = state.getDiseaseRepository().getCubes().get(Color.YELLOW).size() / 24d;
        return min(red, blue, black, yellow);
    };


    /**
     * Evaluate the state based on the number of diseases placed on the map
     */
    public static StateHeuristic Fcp = state -> {
        final double[] p = new double[]{0};
        state.getDiseaseRepository()
                .getCubes().values()
                .forEach((diseases) -> p[0] *= diseases.size()/24.);
        return p[0];
    };


    /**
     * Evaluate the state based on the number of outbreaks occured
     */
    public static StateHeuristic Fb = state -> (1 - (state.getDiseaseRepository().getOutbreaksCount() / 8.));


    public static PlayerCard getDiscardingCard(Player currentPlayer) {
        return currentPlayer.getHand().
                stream().
                min(Comparator.comparingDouble(cc ->
                        StateEvaluation.abilityCure(((CityCard) cc).getCity().getColor(), currentPlayer))).
                orElseGet(() -> (currentPlayer.getHand().get(0)));
    }
}
