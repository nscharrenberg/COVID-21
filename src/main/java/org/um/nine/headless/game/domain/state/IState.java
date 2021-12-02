package org.um.nine.headless.game.domain.state;

import com.rits.cloning.Cloner;
import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.actions.macro.MacroAction;
import org.um.nine.headless.game.domain.actions.macro.MacroActionFactory;

import java.util.Stack;

public interface IState extends Cloneable {
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

    default MacroAction getNextMacro() {
        return MacroActionFactory.init(this).getActions().get(0);
    }
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
            x.getBoardRepository().setState(x);
            x.getPlayerRepository().setState(x);
            x.getCardRepository().setState(x);
            x.getEpidemicRepository().setState(x);
            return x;
        }
    }

}
