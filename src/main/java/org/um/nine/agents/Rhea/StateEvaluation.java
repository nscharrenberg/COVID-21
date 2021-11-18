package org.um.nine.agents.Rhea;

import com.jme3.math.ColorRGBA;
import org.um.nine.domain.Cure;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.roles.ScientistRole;

import static com.google.common.primitives.Doubles.min;

public class StateEvaluation {



    public Ability At = (state,cure) -> {
        if (state.getDiseaseRepository().
                getCures().values()
                .stream().filter(c -> c.getColor().
                        equals(cure.getColor()) && c.isDiscovered()).
                findFirst().orElse(null) != null) return 1;

        return 0;
    };

    public Ability Ac = (state, cure) -> {
        Player p = state.getPlayerRepository().getCurrentPlayer();
        double hpt = p.getHandCards().
                stream().filter(pc ->
                        pc instanceof CityCard cc &&
                                cc.getCity().getColor()
                                        .equals(cure.getColor())).
                count();
        double cd = Cd(p);
        if (hpt >= cd) return 1;
        else return hpt/cd;
    };

    public int Cd (Player player){
        if (player.getRole() instanceof ScientistRole) return 4;
        else return 5;
    }











    public StateHeuristic Fod = state -> (double) state.getDiseaseRepository()
            .getCures().values()
            .stream().filter(Cure::isDiscovered)
            .count() /4;


    public StateHeuristic FoA = state -> {

        final double [] sA = new double[]{0};

        double Nd = (double) state.getDiseaseRepository()
                .getCures().values()
                .stream().filter(Cure::isDiscovered)
                .count();


        state.getDiseaseRepository().
                getCures().values().forEach(cure -> {
                   sA[0] += At.abilityCure(state,cure) + (0.3 * Nd);
                });
        return sA[0] /4 /1.3;
    };


    public StateHeuristic Fca = state -> {
        final double[] s = new double[]{0};
        state.getDiseaseRepository()
                .getCubes().values()
                .forEach((diseases) -> s[0] += diseases.size()/24.);
        return s[0]/4;
    };



    public StateHeuristic Fcm = state -> {
        double red, blue, black, yellow;
        red = state.getDiseaseRepository().getCubes().get(ColorRGBA.Red).size()/24.;
        blue = state.getDiseaseRepository().getCubes().get(ColorRGBA.Blue).size()/24.;
        black = state.getDiseaseRepository().getCubes().get(ColorRGBA.Black).size()/24.;
        yellow = state.getDiseaseRepository().getCubes().get(ColorRGBA.Yellow).size()/24.;
        return min(red,blue,black,yellow);
    };



    public StateHeuristic Fcp = state -> {
        final double[] p = new double[]{0};
        state.getDiseaseRepository()
                .getCubes().values()
                .forEach((diseases) -> p[0] *= diseases.size()/24.);
        return p[0];
    };



    public StateHeuristic Fb = state -> {
        final int [] b = new int[]{0};
        state.getDiseaseRepository().getOutbreakMarker().forEach(outbreakMarker -> {
            if (outbreakMarker.isCurrent())
                b[0] = outbreakMarker.getId();
        });
        return 1- (b[0]/8.);
    };







}
