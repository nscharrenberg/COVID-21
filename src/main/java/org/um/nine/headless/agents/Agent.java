package org.um.nine.headless.agents;

import org.um.nine.headless.agents.state.IState;

public interface Agent{
    void agentDecision(IState state) throws Exception;
}
