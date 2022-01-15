package org.um.nine.headless.agents;


import org.um.nine.headless.agents.rhea.state.IState;
import org.um.nine.headless.agents.utils.Logger;

public interface Agent{
    void agentDecision(IState state) throws Exception;

    Logger getLog();
}
