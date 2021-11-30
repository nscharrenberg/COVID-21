package org.um.nine.headless.game.agents;

import org.um.nine.headless.game.FactoryProvider;
import org.um.nine.headless.game.contracts.repositories.ICardRepository;
import org.um.nine.headless.game.contracts.repositories.IPlayerRepository;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Log {
    private LinkedList<state> log;

    public Log(){
        log = new LinkedList<>();
    }

    public void addStep(String action, City targetLocation){
        log.add(new state(action, targetLocation));
    }

    @Override
    public String toString(){
        StringBuilder info = new StringBuilder();
        log.forEach(state -> {
            info.append(state.toString() + "\n");
        });
        return info.toString();
    }

    public LinkedList<state> getLog() {
        return log;
    }

}
