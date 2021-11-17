package org.um.nine.contracts.repositories;

import org.um.nine.agents.baseline.BaselineAgent;
import org.um.nine.agents.reinforcement.ReinforcementAgent;

public interface IAgentRepository {
    BaselineAgent baselineAgent();
    ReinforcementAgent reinforcementAgent();
}
