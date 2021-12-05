package org.um.nine.headless.agents.rhea;

import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;
import org.um.nine.headless.game.domain.roles.Scientist;
import org.um.nine.headless.game.domain.state.IState;

import java.util.List;

import static com.google.common.primitives.Doubles.min;

public class StateEvaluation {


    /**
     * A(c(t))
     * Ability to discover a cure based on the number of cards of that color in hand
     * Will evaluate over 5 cards, 4 if the role of the current player is Scientist role
     */
    public static double abilityCure(Color color, Player p) {
        double hpt = sameColorCards(p.getHand(),color);
        double cd = Cd(p);
        if (hpt >= cd) return 1;
        else return hpt/cd;
    }

    public static double abilityCure(Color color, List<PlayerCard> cards, Player p) {
        double hpt = sameColorCards(cards,color);
        double cd = Cd(p);
        if (hpt >= cd) return 1;
        else return hpt/cd;
    }

    public static double sameColorCards(List<PlayerCard> cards, Color color){
        return cards.stream().
                filter(c-> c instanceof CityCard cc &&
                        cc.getCity().getColor().equals(color)).
                count();
    }

    /**
     * A(t)
     * Ability to find a cure based on it being already discovered or not
     */
    public static double abilityCure (IState state,Cure cure) {
        if (cure.isDiscovered()) return 1;

        //TODO : this is other players Ac ability to cure the disease.. without share knowledge
        // this value is completely irrelevant
        else return state.getPlayerRepository().
                getPlayers().
                values().
                stream().
                map(p -> abilityCure(cure.getColor(),p)).
                max(Double::compareTo).
                orElse(0d);
    };

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
                getCures().values().forEach(cure -> {
                   sA[0] += abilityCure(state,cure) + (0.3 * Nd);
                });
        return sA[0] /4 * 1.3;
    };


    /**
     * Evaluate the state based on the average number of diseases of each color
     */
    public StateHeuristic Fca = state -> {
        final double[] s = new double[]{0};
        state.getDiseaseRepository()
                .getCubes().values()
                .forEach((diseases) -> s[0] += diseases.size()/24.);
        return s[0]/4;
    };


    /**
     * Evaluate the state based on the minimum number of diseases of each color
     */
    public static StateHeuristic Fcm = state -> {
        double red, blue, black, yellow;
        red = state.getDiseaseRepository().getCubes().get(Color.RED).size()/24.;
        blue = state.getDiseaseRepository().getCubes().get(Color.BLUE).size()/24.;
        black = state.getDiseaseRepository().getCubes().get(Color.BLACK).size()/24.;
        yellow = state.getDiseaseRepository().getCubes().get(Color.YELLOW).size()/24.;
        return min(red,blue,black,yellow);
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
     *  Evaluate the state based on the number of outbreaks occured
     */
    public static StateHeuristic Fb = state -> {
        final int [] b = new int[]{0};
        state.getDiseaseRepository().getOutbreakMarkers().forEach(outbreakMarker -> {
            if (outbreakMarker.isCurrent())
                b[0] = outbreakMarker.getId();
        });
        return 1- (b[0]/8.);
    };







}
