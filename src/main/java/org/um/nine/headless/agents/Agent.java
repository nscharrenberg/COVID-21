package org.um.nine.headless.agents;

import org.um.nine.headless.agents.state.IState;
import org.um.nine.headless.agents.utils.Log;

public interface Agent{
    void agentDecision(IState state) throws Exception;

    Log getLog();
}
