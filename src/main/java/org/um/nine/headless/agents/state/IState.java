package org.um.nine.headless.agents.state;

import com.rits.cloning.Cloner;
import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.um.nine.headless.agents.state.StateEvaluation.abilityCure;

public interface IState {

    default void reset() {
        getBoardRepository().reset();
        getPlayerRepository().reset();
        getCityRepository().reset();
        getDiseaseRepository().reset();
        getCardRepository().reset();
    }

    default void start() {
        getBoardRepository().start(this);
    }

    void setBoardRepository(IBoardRepository iBoardRepository);

    void setDiseaseRepository(IDiseaseRepository iDiseaseRepository);

    IDiseaseRepository getDiseaseRepository();

    void setPlayerRepository(IPlayerRepository iPlayerRepository);

    IPlayerRepository getPlayerRepository();

    IBoardRepository getBoardRepository();

    void setCityRepository(ICityRepository iCityRepository);

    ICityRepository getCityRepository();

    void setCardRepository(ICardRepository iCardRepository);

    ICardRepository getCardRepository();

    void setEpidemicRepository(IEpidemicRepository iEpidemicRepository);

    IEpidemicRepository getEpidemicRepository();
    default boolean isGameLost() {
        if (getDiseaseRepository().isGameOver()) return true;

        var redCubes = getDiseaseRepository().getCubes().get(Color.RED).stream().filter(c -> c.getCity() == null).count();
        var blueCubes = getDiseaseRepository().getCubes().get(Color.BLUE).stream().filter(c -> c.getCity() == null).count();
        var blackCubes = getDiseaseRepository().getCubes().get(Color.BLACK).stream().filter(c -> c.getCity() == null).count();
        var yellowCubes = getDiseaseRepository().getCubes().get(Color.YELLOW).stream().filter(c -> c.getCity() == null).count();

        //TODO: missing some losing conditions
        return redCubes == 0 || blueCubes == 0 || blackCubes == 0 || yellowCubes == 0;
    }
    default boolean isGameWon() {
        return this.getDiseaseRepository().getCures().values().stream().filter(Cure::isDiscovered).count() ==4;
    }

    default IState getClonedState() {
        synchronized (this) {
            Cloner cloner = new Cloner();
            cloner.setDontCloneInstanceOf(Stack.class);
            var x = cloner.deepClone(this);
            return x;
        }
    }

    default PlayerCard[] getDiscardingCard() {
        PlayerCard discarding = null;
        Player player = getPlayerRepository().getCurrentPlayer();
        List<PlayerCard> pc = new ArrayList<>(player.getHand());
        for (int i=0; i< pc.size(); i++){
            PlayerCard card = pc.get(0);
            Color color = ((CityCard)card).getCity().getColor();
            double at = abilityCure(color,pc,player);
            pc.remove(card);
            if (at == abilityCure(color,pc,player)){
                discarding = card;
            }
        }
        if (discarding == null) throw new IllegalStateException();

        return new PlayerCard[]{discarding};
    }

}
